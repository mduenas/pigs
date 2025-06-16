# iOS App Icon Instructions

## Overview
The iOS app icon should use the same Leaning Jowler pig design as the Android version, with a bright orange background (#FF6B35) and pink pig (#FFB6C1).

## Required Sizes for iOS
Create PNG files with the following sizes and place them in the iOS project:

### iPhone
- `Icon-App-20x20@1x.png` - 20x20px
- `Icon-App-20x20@2x.png` - 40x40px  
- `Icon-App-20x20@3x.png` - 60x60px
- `Icon-App-29x29@1x.png` - 29x29px
- `Icon-App-29x29@2x.png` - 58x58px
- `Icon-App-29x29@3x.png` - 87x87px
- `Icon-App-40x40@1x.png` - 40x40px
- `Icon-App-40x40@2x.png` - 80x80px
- `Icon-App-40x40@3x.png` - 120x120px
- `Icon-App-60x60@2x.png` - 120x120px
- `Icon-App-60x60@3x.png` - 180x180px
- `Icon-App-76x76@1x.png` - 76x76px
- `Icon-App-76x76@2x.png` - 152x152px
- `Icon-App-83.5x83.5@2x.png` - 167x167px

### App Store
- `Icon-App-1024x1024@1x.png` - 1024x1024px

## Design Elements
- **Background**: Bright orange circle (#FF6B35)
- **Pig Color**: Light pink (#FFB6C1) with pink accents (#FF69B4)
- **Position**: Leaning Jowler - pig tilted with snout and ear touching ground
- **Eyes**: Dark blue/gray (#2C3E50)
- **Style**: Simple, clean, easily recognizable at small sizes

## Implementation
1. Use the provided SVG (`app_icon.svg`) as the base design
2. Export to PNG at required sizes
3. Ensure the design remains clear and recognizable at smallest sizes
4. Add files to `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/`
5. Update `Contents.json` file in the appiconset folder

## Color Palette
- Primary Background: #FF6B35 (Bright Orange)
- Secondary Background: #E55A2B (Darker Orange)
- Pig Main: #FFB6C1 (Light Pink)
- Pig Accent: #FF69B4 (Hot Pink)
- Pig Detail: #E55A87 (Medium Pink)
- Eyes: #2C3E50 (Dark Blue Gray)
- Ground: #8B4513 (Brown - optional)