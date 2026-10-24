#!/usr/bin/env python3
"""Generate iskanFlix branding assets: logo, TV banner, launcher icon.

Palette: Netflix-style red (#E50914) + white, dark transparent bg.
Style: bold sans wordmark "iskanFlix" (red), Netflix-vibe.
"""
from PIL import Image, ImageDraw, ImageFont

RED = (229, 9, 20, 255)       # netflix red
DARK = (14, 14, 18, 255)      # background dark #0E0E12
FONT_BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"


def make_logo(w=800, h=221, out=""):
    """Horizontal wordmark logo. Auto-fit font to canvas width."""
    im = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    text = "iskanFlix"
    # fit font so text width <= 92% canvas
    fs = int(h * 0.9)
    from PIL import ImageFont
    font = ImageFont.truetype(FONT_BOLD, fs)
    while True:
        bbox = d.textbbox((0, 0), text, font=font)
        if bbox[2] - bbox[0] <= w * 0.92:
            break
        fs = int(fs * 0.95)
        font = ImageFont.truetype(FONT_BOLD, fs)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (w - tw) // 2 - bbox[0]
    y = (h - th) // 2 - bbox[1]
    bbox_i = d.textbbox((0, 0), "iskan", font=font)
    split_x = x + (bbox_i[2] - bbox_i[0])
    d.text((x + 3, y + 3), text, font=font, fill=(0, 0, 0, 160))
    d.text((x, y), "iskan", font=font, fill=RED)
    d.text((split_x, y), "Flix", font=font, fill=(255, 255, 255, 255))
    im.save(out, "PNG")
    print("logo:", out, im.size, "textw:", tw)


def make_banner(w=640, h=360, out=""):
    """Android TV banner 640x360 (16:9), centered wordmark."""
    im = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    fs = int(h * 0.22)
    from PIL import ImageFont
    font = ImageFont.truetype(FONT_BOLD, fs)
    d = ImageDraw.Draw(im)
    text = "iskanFlix"
    bbox = d.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (w - tw) // 2 - bbox[0]
    y = (h - th) // 2 - bbox[1]
    d.text((x + 3, y + 3), text, font=font, fill=(0, 0, 0, 160))
    bbox_i = d.textbbox((0, 0), "iskan", font=font)
    d.text((x, y), "iskan", font=font, fill=RED)
    d.text((x + (bbox_i[2] - bbox_i[0]), y), "Flix", font=font, fill=(255, 255, 255, 255))
    im.save(out, "PNG")
    print("banner:", out, im.size)


def make_launcher(w=1024, h=1024, out=""):
    """Square launcher icon: dark rounded bg + centered wordmark."""
    im = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    # rounded rect background
    radius = int(w * 0.18)
    d.rounded_rectangle([0, 0, w - 1, h - 1], radius=radius, fill=DARK)
    # wordmark centered, scaled to ~78% width
    fs = int(w * 0.20)
    from PIL import ImageFont
    font = ImageFont.truetype(FONT_BOLD, fs)
    d2 = ImageDraw.Draw(im)
    text = "iskanFlix"
    bbox = d.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = (w - tw) // 2 - bbox[0]
    y = (h - th) // 2 - bbox[1]
    bbox_i = d.textbbox((0, 0), "iskan", font=font)
    split_x = x + (bbox_i[2] - bbox_i[0])
    d.text((x, y), "iskan", font=font, fill=RED)
    d.text((split_x, y), "Flix", font=font, fill=(255, 255, 255, 255))
    im.save(out, "PNG")
    print("launcher:", out, im.size)


if __name__ == "__main__":
    import sys
    base = "/root/idlix-androidtv"
    make_logo(800, 221, f"{base}/app-tv/src/main/res/drawable/iskanflix_logo.png")
    make_logo(800, 221, f"{base}/app-mobile/src/main/res/drawable/iskanflix_logo.png")
    make_banner(640, 360, f"{base}/app-tv/src/main/res/drawable/ic_banner.png")
    make_banner(640, 360, f"{base}/app-mobile/src/main/res/drawable/ic_banner.png")
    make_launcher(1024, 1024, f"{base}/app-tv/src/main/res/mipmap/ic_launcher.png")
    make_launcher(1024, 1024, f"{base}/app-mobile/src/main/res/mipmap/ic_launcher.png")
