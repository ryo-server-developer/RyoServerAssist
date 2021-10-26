package com.ryoserver.World.SimpleRegion

import com.ryoserver.Inventory.Item.getItem
import com.ryoserver.util.WorldGuardWrapper
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.Location
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.flags.{Flags, StateFlag}
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.ChatColor._
import org.bukkit.entity.Player

import scala.jdk.CollectionConverters._

class RegionSettingMenu {

  def openMenu(p:Player): Unit = {
    val worldGuard = new WorldGuardWrapper
    val loc = p.getLocation()
    if (!worldGuard.isProtected(loc) || !worldGuard.isOwner(p,loc)) {
      p.sendMessage(ChatColor.RED + "この場所はあなたの保護ではありません！")
      return
    }
    val set = worldGuard.getRegion(loc).head
    val inv = Bukkit.createInventory(null,54,"保護設定メニュー")
    inv.setItem(1,getItem(Material.TNT,s"${RED}${BOLD}保護を削除します。",
      List(s"${RED}${BOLD}取扱注意！",s"${RED}${BOLD}保護範囲を削除します。").asJava))
    inv.setItem(3,getItem(Material.OAK_DOOR,s"${GREEN}フラグ:useを切り替えます。",
      List(s"${GRAY}ドアやボタンの使用を許可します。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.USE)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    inv.setItem(5,getItem(Material.OAK_BUTTON,s"${GREEN}フラグ:interactを切り替えます。",
      List(s"${GRAY}スイッチの使用を許可します。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.INTERACT)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    inv.setItem(7,getItem(Material.CHEST,s"${GREEN}フラグ:chest-accessを切り替えます。",
      List(s"${GRAY}チェストの使用を許可します。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.CHEST_ACCESS)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    inv.setItem(19,getItem(Material.WHITE_BED,s"${GREEN}フラグ:sleepを許可します。",
      List(s"${GRAY}ベットで眠る許可をします。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.SLEEP)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    inv.setItem(21,getItem(Material.MINECART,s"${GREEN}フラグ:vehicle-placeを許可します。",
      List(s"${GRAY}トロッコ、ボードの設置を許可します。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.PLACE_VEHICLE)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    inv.setItem(23,getItem(Material.OAK_BOAT,s"${GREEN}フラグ:vehicle-destroyを許可します。",
      List(s"${GRAY}トロッコ、ボードの破壊を許可します。",
        s"${GRAY}状態:${if (getFlagStatus(set,Flags.DESTROY_VEHICLE)) s"${AQUA}許可" else s"${RED}拒否"}").asJava))
    p.openInventory(inv)
  }

  def getFlagStatus(set:ProtectedRegion, flag:StateFlag): Boolean = {
    set.getFlags.getOrDefault(flag,"DENY").toString == "ALLOW"
  }

}
