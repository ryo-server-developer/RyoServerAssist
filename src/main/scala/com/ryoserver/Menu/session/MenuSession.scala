package com.ryoserver.Menu.session

import com.ryoserver.Menu.Button.Button
import com.ryoserver.Menu.MenuFrame
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.{Inventory, InventoryHolder}

abstract class MenuSession(frame: MenuFrame) extends InventoryHolder {
  private val sessionInventory = frame.createMenu(this)

  val currentLayout:Map[Int,Button]

  def openInventory(p: Player): Unit = {
    p.openInventory(getInventory)
  }

  def setLayout(layout: Map[Int,Button]): Unit = {
    layout.foreach{case (index,button) =>
      sessionInventory.setItem(index,button.itemStack)
    }
  }

  def runMotion(index: Int,clickEvent: InventoryClickEvent): Unit = {
    if (currentLayout.contains(index)) currentLayout(index).runMotion(clickEvent)
  }

  override def getInventory: Inventory = sessionInventory
}
