### chars: 
> recipes preview gui: \uF114 ~ \uF117
> 
> some blank: \uF170 ~ \uF200

### custom model data
> levels: paper(114500 ~ 114513)
> 
> gui components: paper(114514 ~ 114599)
> 
> substrate: tropical_fish(114600 ~ 114699)
>
> potions: potion(114700 ~ 114799)
> 
> yeast: paper(114800 ~ 114899)


# 特别鸣谢
## 技术支持: [Caizii](https://github.com/Caishangqi)
## 美术支持: 桶

### todo list
- [ ] cancel crafting
- [ ] craft recipes
- [ ] packet listener
- [ ] resource sender

### tree data structure
```text
item category: vanilla / default (additional: itemsadder|mmoitem)

item
├── effect: PotionMeta (~*output todo define)
├── craft: shiranai ([^output] todo define)
├── restores: custom (todo fulfill)
├── lore: (Optional)
│   ├── xxx (blank if null)
│   ├── {text: xxx}
│   └── {text: xxx, color:[x,x,x]}
├── display: (Optional fallback: Material Name)
│   ├── name: xxx (allow cc.translate)
│   └── color: [x,x,x] (Nullable)
└── custom-model-data: float without decimal (Number == null ? 0 : Number, only work with Vanilla Item)
```