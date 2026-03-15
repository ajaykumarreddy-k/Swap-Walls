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
import hashlib
from pathlib import Path
from datetime import date
 
try:
    from PIL import Image, ImageFile, ExifTags
    # Allow PIL to load truncated images
    ImageFile.LOAD_TRUNCATED_IMAGES = True
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

def get_file_hash(filepath: Path) -> str:
    """Calculates MD5 hash of a file to detect duplicates."""
    hasher = hashlib.md5()
    with open(filepath, "rb") as f:
        # Read in chunks for large files
        for chunk in iter(lambda: f.read(4096), b""):
            hasher.update(chunk)
    return hasher.hexdigest()

def is_copyrighted(img: Image.Image, filepath: Path) -> bool:
    """Checks if an image is copyrighted based on metadata and filename."""
    keywords = ["copyright", "(c)", "all rights reserved", "rights reserved"]
    
    # Check filename
    filename_lower = filepath.name.lower()
    if any(k in filename_lower for k in keywords):
        return True
        
    # Check EXIF Metadata
    try:
        exif = img.getexif()
        if exif:
            for tag_id, value in exif.items():
                tag_name = ExifTags.TAGS.get(tag_id, tag_id)
                if tag_name in ["Copyright", "Artist"]:
                    if isinstance(value, str) and any(k in value.lower() for k in keywords):
                        return True
    except Exception:
        pass # Metadata might be corrupted
        
    return False
 
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
 
    # To track duplicates across all categories
    seen_hashes = set()

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
            
            try:
                # Duplicate Check
                img_hash = get_file_hash(img_path)
                if img_hash in seen_hashes:
                    print(f"  !! Skipping Duplicate: {img_path.name}")
                    continue
                
                print(f"  -> {img_path.name}")
                stem = img_path.stem.lower().replace(" ", "_")
                out_file = f"{stem}.jpg"
                out_thumb = f"{stem}_thumb.jpg"
                
                # Process Image
                with Image.open(img_path) as img_orig:
                    # Copyright Check
                    if is_copyrighted(img_orig, img_path):
                        print(f"  XX Flagged Copyrighted: {img_path.name} (DELETING)")
                        # Close file before deleting if necessary (handled by with block)
                        img_orig.close()
                        os.remove(img_path)
                        continue

                    # Convert to RGB early
                    img_rgb = img_orig.convert("RGB")
                    
                    # Save Full
                    img_full = resize_image(img_rgb.copy(), FULL_MAX_WIDTH, FULL_MAX_HEIGHT)
                    img_full.save(cat_output / out_file, "JPEG", quality=QUALITY)
                    
                    # Save Thumb
                    img_thumb = resize_image(img_rgb.copy(), THUMB_WIDTH, THUMB_HEIGHT)
                    img_thumb.save(cat_output / out_thumb, "JPEG", quality=QUALITY)
                
                # Color (from processed full image)
                color = get_dominant_color(str(cat_output / out_file))
                
                wallpapers.append({
                    "id": f"{cat_id}_{count:03d}",
                    "title": stem.replace("_", " ").title(),
                    "file": out_file,
                    "thumb": out_thumb,
                    "color": color
                })
                
                seen_hashes.add(img_hash)
                count += 1
                
            except Exception as e:
                print(f"  !! Error processing {img_path.name}: {e}")
                continue
            
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
