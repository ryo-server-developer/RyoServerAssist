package com.ryoserver.Stack

import com.ryoserver.Menu.createMenu
import com.ryoserver.RyoServerAssist
import com.ryoserver.Stack.ItemData.itemData
import com.ryoserver.Stack.PlayerCategory.{getSelectedCategory, setSelectedCategory}
import com.ryoserver.Stack.PlayerData.playerData
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.collection.mutable

class StackGUIEvent(ryoServerAssist: RyoServerAssist) extends Listener {

  @EventHandler
  def onClick(e:InventoryClickEvent): Unit = {
    val title = e.getView.getTitle
    val inv = e.getView.getTopInventory
    val gui = new StackGUI(ryoServerAssist)
    val p = e.getWhoClicked.asInstanceOf[Player]
    val isRightClick = e.getClick.isRightClick
    val permission = p.hasPermission("ryoserverassist.stack")
    val isEdit = isRightClick && permission
    val index = e.getSlot
    val data = new StackData(ryoServerAssist)
    if (title.equalsIgnoreCase("stackカテゴリ選択") && e.getClickedInventory == e.getView.getTopInventory) {
      e.setCancelled(true)
       index match {
        case 11 =>
          gui.openStack(p, 1, "block", isEdit)
          setSelectedCategory(p,"block")
        case 13 =>
          gui.openStack(p, 1, "item", isEdit)
          setSelectedCategory(p,"item")
        case 15 =>
          gui.openStack(p, 1, "gachaItem", isEdit)
           setSelectedCategory(p,"gachaItem")
        case 19 =>
          gui.openStack(p, 1, "tool", isEdit)
          setSelectedCategory(p,"tool")
        case 21 =>
          gui.openStack(p, 1, "food", isEdit)
          setSelectedCategory(p,"food")
        case 23 =>
          gui.openStack(p, 1, "redstone", isEdit)
          setSelectedCategory(p,"redstone")
        case 25 =>
          gui.openStack(p, 1, "plant", isEdit)
          setSelectedCategory(p,"plant")
        case 36 =>
          p.openInventory(createMenu.menu(p, ryoServerAssist))
        case 40 =>
          data.toggleAutoStack(p)
          gui.openCategorySelectGUI(p)
        case 44 =>
          p.getInventory.getContents.foreach(item => {
            if (item != null) {
              if (new StackData(ryoServerAssist).checkItemList(item)) {
                data.addStack(item,p)
                p.getInventory.removeItem(item)
              }
            }
          })
          p.sendMessage(ChatColor.AQUA + "インベントリ内のアイテムをすべてStackに収納しました。")
        case _ =>
      }
    } else if (title.contains("stackアイテム追加メニュー:") && e.getClickedInventory == e.getView.getTopInventory) {
      val nowPage = title.replace("stackアイテム追加メニュー:","").toInt
      if (index == 49) {
        e.setCancelled(true)
        var invIndex = 0
        var invItem = ""
        inv.getContents.foreach(is => {
          if (invIndex < 45) {
            val config = new YamlConfiguration
            config.set("i",is)
            invItem += config.saveToString() + ";"
          }
          invIndex += 1
        })
        data.editItemList(getSelectedCategory(p),nowPage,invItem)
        p.sendMessage(ChatColor.AQUA  + "カテゴリリスト:" + getSelectedCategory(p) + "を編集しました。")
        new StackGUI(ryoServerAssist).loadStackPage()
        ItemList.stackList = mutable.Map.empty
        ItemList.loadItemList(ryoServerAssist)
      } else if (index == 45) {
        if (nowPage != 1) {
          gui.openAddGUI(p,nowPage - 1,getSelectedCategory(p))
        } else if (nowPage == 1) {
          p.openInventory(createMenu.menu(p,ryoServerAssist))
        }
      } else if (index == 53) {
        gui.openAddGUI(p,nowPage + 1,getSelectedCategory(p))
      }
    } else if (title.contains("stack") && e.getClickedInventory == e.getView.getTopInventory) {
      e.setCancelled(true)
      val nowPage = title.replace("stack:","").replace("[Edit]","").toInt
      index match {
        case 45 =>
          val backPage = nowPage - 1
          if (backPage == 0) gui.openCategorySelectGUI(p)
          else gui.openStack(p,backPage,getSelectedCategory(p),isEdit)
        case 49 =>
          if (title.contains("Edit")) gui.openAddGUI(p,1,getSelectedCategory(p))
        case 53 =>
          gui.openStack(p,nowPage + 1,getSelectedCategory(p),isEdit)
        case _ =>
          val is = inv.getItem(index)
          if (!itemData.contains(p.getName) || !itemData(p.getName).contains(is)) return
          if (e.getClick.isRightClick) {
            data.addItemToPlayer(p,ItemData.itemData(p.getName)(is),1)
          } else if (e.getClick.isLeftClick) {
            data.addItemToPlayer(p,ItemData.itemData(p.getName)(is),is.getType.getMaxStackSize)
          }
          val config = new YamlConfiguration
          config.set("i",ItemData.itemData(p.getName)(is))
          gui.openStack(p,nowPage,getSelectedCategory(p),title.contains("Edit"))
        }
      }
  }

}
