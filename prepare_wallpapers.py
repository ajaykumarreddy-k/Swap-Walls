#!/usr/bin/env python3
"""
prepare_wallpapers.py
---------------------
Scans 'input/' for subfolders (categories), resizes images, 
creates thumbnails, and generates a structured manifest.json.
 
Usage:
    python prepare_wallpapers.py
"""
 
import os
import sys
import json
import shutil
from pathlib import Path
from datetime import date
 
try:
    from PIL import Image
    from colorthief import ColorThief
except ImportError:
    print("Run first: pip install Pillow colorthief")
    sys.exit(1)
 
# Settings
FULL_MAX_WIDTH  = 1080
FULL_MAX_HEIGHT = 1920
THUMB_WIDTH     = 400
THUMB_HEIGHT    = 700
QUALITY         = 88
 
def resize_image(img: Image.Image, max_w: int, max_h: int) -> Image.Image:
    img.thumbnail((max_w, max_h), Image.LANCZOS)
    return img
 
def get_dominant_color(filepath: str) -> str:
    try:
        ct = ColorThief(filepath)
        r, g, b = ct.get_color(quality=1)
        return "#{:02X}{:02X}{:02X}".format(r, g, b)
    except Exception:
        return "#1565C0"
 
def prepare_assets():
    input_root = Path("images")
    output_root = Path("output")
    manifest_path = Path("manifest.json")
 
    if not input_root.exists():
        input_root.mkdir()
        print("Created 'images/' folder. Please add category subfolders (e.g. images/anime/) and run again.")
        return
 
    manifest = {"version": 1, "updated": str(date.today()), "categories": []}
    supported = {".jpg", ".jpeg", ".png", ".webp"}
 
    # Scan for subdirectories in images/
    categories = [d for d in input_root.iterdir() if d.is_dir()]
    
    if not categories:
        print("No category subfolders found in 'images/'. Example: images/anime/")
        return
 
    for cat_dir in categories:
        cat_id = cat_dir.name.lower()
        print(f"Processing Category: {cat_dir.name}")
        
        cat_output = output_root / "wallpapers" / cat_id
        cat_output.mkdir(parents=True, exist_ok=True)
        
        wallpapers = []
        count = 1
        
        for img_path in sorted(cat_dir.iterdir()):
            if img_path.suffix.lower() not in supported:
                continue
            
            print(f"  -> {img_path.name}")
            stem = img_path.stem.lower().replace(" ", "_")
            out_file = f"{stem}.jpg"
            out_thumb = f"{stem}_thumb.jpg"
            
            # Save Full
            img = Image.open(img_path).convert("RGB")
            img = resize_image(img, FULL_MAX_WIDTH, FULL_MAX_HEIGHT)
            img.save(cat_output / out_file, "JPEG", quality=QUALITY)
            
            # Save Thumb
            img_t = Image.open(img_path).convert("RGB")
            img_t = resize_image(img_t, THUMB_WIDTH, THUMB_HEIGHT)
            img_t.save(cat_output / out_thumb, "JPEG", quality=QUALITY)
            
            # Color
            color = get_dominant_color(str(cat_output / out_file))
            
            wallpapers.append({
                "id": f"{cat_id}_{count:03d}",
                "title": stem.replace("_", " ").title(),
                "file": out_file,
                "thumb": out_thumb,
                "color": color
            })
            count += 1
            
        manifest["categories"].append({
            "id": cat_id,
            "name": cat_dir.name.title(),
            "icon": "image",
            "wallpapers": wallpapers
        })
 
    with open(manifest_path, "w") as f:
        json.dump(manifest, f, indent=2)
    
    print(f"\nSuccess! manifest.json updated with {len(categories)} categories.")
    print(f"Assets created in {output_root}/wallpapers/")
 
if __name__ == "__main__":
    prepare_assets()
