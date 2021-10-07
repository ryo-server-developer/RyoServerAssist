package com.ryoserver.Quest

import com.ryoserver.Menu.createMenu
import com.ryoserver.RyoServerAssist
import com.ryoserver.Title.giveTitle
import org.bukkit.{ChatColor, Material, Sound}
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryCloseEvent}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.{Inventory, ItemStack}


class QuestSelectInventoryEvent(ryoServerAssist: RyoServerAssist) extends Listener {

  @EventHandler
  def onClick(e: InventoryClickEvent): Unit = {
    if (e.getView.getTitle == "クエスト選択") {
      e.setCancelled(true)
      val p = e.getWhoClicked.asInstanceOf[Player]
      val questData = new QuestData(ryoServerAssist)
      val questNames = questData.loadQuest(p)
      val lottery = new LotteryQuest()
      e.getSlot match {
        case 1 =>
          lottery.questName = questNames(0)
          lottery.loadQuestData()
          questData.selectQuest(p, lottery)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
        case 3 =>
          lottery.questName = questNames(1)
          lottery.loadQuestData()
          questData.selectQuest(p, lottery)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
        case 5 =>
          lottery.questName = questNames(2)
          lottery.loadQuestData()
          questData.selectQuest(p, lottery)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
        case 7 =>
          lottery.questName = questNames(3)
          lottery.loadQuestData()
          questData.selectQuest(p, lottery)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
        case 22 =>
          questData.resetQuest(p)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
          p.playSound(p.getLocation, Sound.BLOCK_ANVIL_DESTROY, 1, 1)
        case 26 =>
          p.openInventory(createMenu.menu(p,ryoServerAssist))
          p.playSound(p.getLocation(), Sound.BLOCK_WOOL_PLACE, 1, 1)
        case _ =>
      }
    } else if (e.getView.getTitle == "納品") {
      val index = e.getSlot
      val inv = e.getInventory
      val p = e.getWhoClicked.asInstanceOf[Player]
      val questData = new QuestData(ryoServerAssist)
      if (index == 45 || index == 46 || index == 53) e.setCancelled(true)
      index match {
        case 46 =>
          var remainingItems:Array[ItemStack] = Array.empty
          var invItems:Array[ItemStack] = Array.empty
          //クエスト終了に必要な残りの納品アイテムを取得する
          questData.getSelectedQuestRemaining(p).split(";").foreach(remainingItem => {
            val itemData = remainingItem.split(":")
            remainingItems :+= new ItemStack(Material.matchMaterial(itemData(0)),itemData(1).toInt)
          })
          //インベントリの中身をすべて取得
          inv.getContents.foreach(invItem => {
            invItems :+= invItem
          })
          //納品アイテムを更新
          remainingItems.foreach(remainingItem => {
            invItems.foreach(invItem => {
              if (invItem != null) {
                if (invItem.getType == remainingItem.getType && remainingItem.getAmount > 0) {
                  remainingItems = remainingItems.filterNot(_ == remainingItem) //一旦削除
                  val amount = remainingItem.getAmount - invItem.getAmount
                  if (amount < 0) {
                    inv.removeItem(remainingItem)
                    remainingItem.setAmount(0)
                  } else {
                    inv.removeItem(invItem)
                    remainingItem.setAmount(amount)
                  }
                  remainingItems :+= remainingItem
                }
              }
            })
          })

          //残りの数を設定
          var questDone = true
          var remainingItem_str = ""
          remainingItems.foreach(remainingItem => {
            remainingItem_str += remainingItem.getType.name() + ":" + remainingItem.getAmount + ";"
            if (remainingItem.getAmount != 0) questDone = false
          })
          questData.setSelectedQuestItemRemaining(p,remainingItem_str)

          if (questDone) {
            p.sendMessage(ChatColor.AQUA + "おめでとうございます！クエストが完了しました！")
            p.playSound(p.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,1)
            questData.questClear(p)
            new QuestSelectInventory(ryoServerAssist).selectInventory(p)
            new giveTitle(ryoServerAssist).questClearNumber(p)
          } else {
            p.sendMessage(ChatColor.AQUA + "納品しました。")
            new QuestSelectInventory(ryoServerAssist).selectInventory(p)
          }
        case 53 =>
          questData.resetQuest(p)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
          p.playSound(p.getLocation, Sound.BLOCK_ANVIL_DESTROY, 1, 1)
        case _ =>
      }
    } else if (e.getView.getTitle == "討伐") {
      val p = e.getWhoClicked.asInstanceOf[Player]
      val questData = new QuestData(ryoServerAssist)
      e.setCancelled(true)
      e.getSlot match {
        case 53 =>
          questData.resetQuest(p)
          new QuestSelectInventory(ryoServerAssist).selectInventory(p)
          p.playSound(p.getLocation, Sound.BLOCK_ANVIL_DESTROY, 1, 1)
        case _ =>
      }
    }
  }

  @EventHandler
  def closeInventory(e: InventoryCloseEvent): Unit = {
    if (e.getView.getTitle == "納品") returnItem(e.getInventory,e.getPlayer.asInstanceOf[Player])
  }

  def returnItem(inv:Inventory,p: Player): Unit = {
    var isSend = false
    inv.getContents.foreach(is=> {
      if (is != null) {
        if (is.getItemMeta != inv.getItem(45).getItemMeta && is.getItemMeta != inv.getItem(46).getItemMeta
          && is.getItemMeta != inv.getItem(53).getItemMeta) {
          p.getWorld.dropItem(p.getLocation(), is)
          if (!isSend) {
            p.sendMessage(ChatColor.RED + "不要なアイテムを返却しました。")
            isSend = true
          }
        }
      }
    })
  }

}
