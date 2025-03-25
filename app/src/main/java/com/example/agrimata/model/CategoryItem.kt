package com.example.agrimata.model

sealed class CategoryItem(val category: String) {
    object Category1 : CategoryItem("Meat & Poultry")
    object Category2 : CategoryItem("Dairy & Eggs")
    object Category3 : CategoryItem("Grains & Cereals")
    object Category4 : CategoryItem("Legumes & Nuts")
    object Category5 : CategoryItem("Honey & Natural Sweeteners")
    object Category6 : CategoryItem("Organic & Specialty Foods")
    object Category7 : CategoryItem("Oils & Condiments")
    object Category8 : CategoryItem("Farm Supplies & Tools")
    object Category9 : CategoryItem("Handmade & Artisanal Goods")
    object Category10 : CategoryItem("Flowers & Plants")
    object Category11 : CategoryItem("Livestock & Poultry")
}

val listOfCategoryItems = listOf(
    CategoryItem.Category1,
    CategoryItem.Category2,
    CategoryItem.Category3,
    CategoryItem.Category4,
    CategoryItem.Category5,
    CategoryItem.Category6,
    CategoryItem.Category7,
    CategoryItem.Category8,
    CategoryItem.Category9,
    CategoryItem.Category10,
    CategoryItem.Category11,
)
