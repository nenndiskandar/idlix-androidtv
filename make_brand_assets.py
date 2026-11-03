#!/usr/bin/env python3
"""
Generate iskanFlix branding assets: logo, TV banner, launcher icon.
Style: Netflix authentic aesthetic (all-caps ISKANFLIX, Bebas Neue Bold,
vibrant Netflix Red #E50914, signature upward arched baseline).
"""
from PIL import Image, ImageDraw, ImageFont

FONT_BOLD = "/usr/share/fonts/opentype/bebas-neue/BebasNeue-Bold.otf"
NETFLIX_RED = (229, 9, 20, 255)       # #E50914
NETFLIX_BLACK = (18, 18, 22, 255)     # #121216


def create_netflix_wordmark(text="ISKANFLIX", target_w=800, target_h=221, arc_ratio=0.09, fill_ratio=0.96):
    """
    Renders the Netflix-style wordmark:
    - High-res super-sampled rendering (4x)
    - Pure Netflix Red #E50914
    - Flat top, curved upward bottom arc
    - Maximized bounding box to fill target dimensions
    """
    scale = 4
    w_hi = target_w * scale
    h_hi = target_h * scale

    fs_low, fs_high = 10, h_hi * 2
    best_fs = fs_low

    dummy = Image.new("RGBA", (1, 1))
    d = ImageDraw.Draw(dummy)

    for _ in range(25):
        mid = (fs_low + fs_high) // 2
        f = ImageFont.truetype(FONT_BOLD, int(mid))
        bbox = d.textbbox((0, 0), text, font=f)
        tw = bbox[2] - bbox[0]
        th = bbox[3] - bbox[1]

        if tw <= w_hi * fill_ratio and th <= h_hi * 0.94:
            best_fs = mid
            fs_low = mid + 1
        else:
            fs_high = mid - 1

    font = ImageFont.truetype(FONT_BOLD, int(best_fs))
    bbox = d.textbbox((0, 0), text, font=font)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]

    # Render flat text onto tight image
    flat_img = Image.new("RGBA", (tw, th), (0, 0, 0, 0))
    d_flat = ImageDraw.Draw(flat_img)
    d_flat.text((-bbox[0], -bbox[1]), text, font=font, fill=NETFLIX_RED)

    # Apply upward bottom arc (Netflix signature curve)
    # y=0 stays at 0. y=th moves to th - delta(u)
    warped = Image.new("RGBA", (tw, th), (0, 0, 0, 0))
    src_pixels = flat_img.load()
    dst_pixels = warped.load()

    max_arch = th * arc_ratio

    for x in range(tw):
        u = (2.0 * x - tw) / tw
        delta = max_arch * (1.0 - u * u)
        col_h = th - delta
        if col_h <= 0:
            continue
        for y in range(int(col_h)):
            src_y = int(y * th / col_h)
            if 0 <= src_y < th:
                dst_pixels[x, y] = src_pixels[x, src_y]

    # Crop tight before final placement
    crop_bbox = warped.getbbox()
    if crop_bbox:
        warped = warped.crop(crop_bbox)

    dest_w = int(target_w * fill_ratio)
    dest_h = int(warped.height * (dest_w / warped.width))

    if dest_h > target_h * 0.95:
        dest_h = int(target_h * 0.95)
        dest_w = int(warped.width * (dest_h / warped.height))

    scaled = warped.resize((dest_w, dest_h), Image.Resampling.LANCZOS)

    final = Image.new("RGBA", (target_w, target_h), (0, 0, 0, 0))
    pos_x = (target_w - dest_w) // 2
    pos_y = (target_h - dest_h) // 2
    final.paste(scaled, (pos_x, pos_y), scaled)

    return final


def make_logo(w=800, h=221, out=""):
    """Transparent background wordmark logo for in-app header / drawer."""
    im = create_netflix_wordmark("ISKANFLIX", w, h, arc_ratio=0.09, fill_ratio=0.96)
    im.save(out, "PNG")
    print("logo:", out, im.size, "bbox:", im.getbbox())


def make_banner(w=640, h=360, out=""):
    """Android TV banner (16:9) with Netflix dark background and prominent wordmark."""
    im = Image.new("RGBA", (w, h), NETFLIX_BLACK)
    mark_w = int(w * 0.88)
    mark_h = int(h * 0.45)
    mark = create_netflix_wordmark("ISKANFLIX", mark_w, mark_h, arc_ratio=0.09, fill_ratio=0.98)
    bx = (w - mark_w) // 2
    by = (h - mark_h) // 2
    im.paste(mark, (bx, by), mark)
    im.save(out, "PNG")
    print("banner:", out, im.size, "bbox:", im.getbbox())


def make_launcher(w=1024, h=1024, out=""):
    """
    App launcher icon:
    Rounded square Netflix Black with prominent bold arched ISKANFLIX wordmark.
    """
    im = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    radius = int(w * 0.18)
    d.rounded_rectangle([0, 0, w - 1, h - 1], radius=radius, fill=NETFLIX_BLACK)

    # Wordmark in center: ~88% of icon width
    mark_w = int(w * 0.88)
    mark_h = int(h * 0.35)
    mark = create_netflix_wordmark("ISKANFLIX", mark_w, mark_h, arc_ratio=0.09, fill_ratio=0.98)
    mx = (w - mark_w) // 2
    my = (h - mark_h) // 2
    im.paste(mark, (mx, my), mark)

    im.save(out, "PNG")
    print("launcher:", out, im.size, "bbox:", im.getbbox())


if __name__ == "__main__":
    base = "/root/idlix-androidtv"
    make_logo(800, 221, f"{base}/app-tv/src/main/res/drawable/iskanflix_logo.png")
    make_logo(800, 221, f"{base}/app-mobile/src/main/res/drawable/iskanflix_logo.png")
    make_banner(640, 360, f"{base}/app-tv/src/main/res/drawable/ic_banner.png")
    make_banner(640, 360, f"{base}/app-mobile/src/main/res/drawable/ic_banner.png")
    make_launcher(1024, 1024, f"{base}/app-tv/src/main/res/mipmap/ic_launcher.png")
    make_launcher(1024, 1024, f"{base}/app-mobile/src/main/res/mipmap/ic_launcher.png")
