package com.ryoserver.Inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.inventory.meta.ItemMeta

object Item {
  val getItem:(Material,String,java.util.List[String]) => ItemStack = (material: Material,name:String,lore: java.util.List[String]) => {
    val itemStack:ItemStack = new ItemStack(material)
    val itemMeta:ItemMeta = itemStack.getItemMeta
    itemMeta.setDisplayName(name)
    itemMeta.setLore(lore)
    itemStack.setItemMeta(itemMeta)
    itemStack
  }

  val getGachaItem:(Material,String,java.util.List[String]) => ItemStack = (material: Material,name:String,lore: java.util.List[String]) => {
    val itemStack:ItemStack = new ItemStack(material)
    val itemMeta:ItemMeta = itemStack.getItemMeta
    itemMeta.setDisplayName(name)
    itemMeta.setLore(lore)
    itemMeta.addEnchant(Enchantment.MENDING,1,false)
    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
    itemStack.setItemMeta(itemMeta)
    itemStack
  }

}
