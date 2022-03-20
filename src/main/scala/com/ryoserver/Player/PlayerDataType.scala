package com.ryoserver.Player

import com.ryoserver.Level.CalLv
import com.ryoserver.Player.PlayerData.PlayerDataRepository
import com.ryoserver.SkillSystems.Skill.EffectSkill.EffectSkills
import org.bukkit.OfflinePlayer

import java.util.Date

case class PlayerDataType(lastLogin: Date,lastLogout: Option[Date],
                          level: Int, exp: Double,
                          ranking: Int, backRankingExpDiff: Double, nextRankingExpDiff: Double,
                          lastDistributionReceived: Int,skillPoint: Double, loginDays: Int,
                          consecutiveLoginDays: Int, questClearTimes: Int,
                          gachaTickets: Int, gachaPullNumber: Int,
                          skillOpenPoint: Int, openedSkills: Set[EffectSkills],
                          voteNumber: Int, reVoteNumber: Int, lastVote: Date,specialSkillOpenPoint: Int,
                          openedSpecialSkills: Set[String], openedTitles: Set[String],
                          selectedTitle: Option[String], autoStack: Boolean,
                          Twitter: Option[String], Discord: Option[String], Word: Option[String]) {

  def setLastLogoutNow(): PlayerDataType = this.copy(lastLogout = Option(new Date))

  def setExp(exp: Double): PlayerDataType = this.copy(level = new CalLv().getLevel(exp), exp = exp)

  def addExp(addExp: Double): PlayerDataType = this.copy(level = new CalLv().getLevel(exp + addExp),exp = exp + addExp)

  def setRanking(ranking: Int): PlayerDataType = this.copy(ranking = ranking)

  def setLastDistributionReceived(num: Int): PlayerDataType = this.copy(lastDistributionReceived = num)

  def setSkillPoint(skillPoint: Double): PlayerDataType = this.copy(skillPoint = skillPoint)

  def setLoginNumber(loginDays: Int): PlayerDataType = this.copy(loginDays = loginDays)

  def setConsecutiveLoginDays(consecutiveLoginNum: Int): PlayerDataType = this.copy(consecutiveLoginDays = consecutiveLoginNum)

  def addQuestClearTimes(addNum: Int): PlayerDataType = this.copy(questClearTimes = questClearTimes + addNum)

  def addGachaTickets(addGachaTicketsAmount: Int): PlayerDataType = this.copy(gachaTickets = gachaTickets + addGachaTicketsAmount)

  def removeGachaTicket(removeGachaTicketsAmount: Int): PlayerDataType = this.copy(gachaTickets = gachaTickets - removeGachaTicketsAmount)

  def setGachaPullNumber(gachaPullNumber: Int): PlayerDataType = this.copy(gachaPullNumber = gachaPullNumber)

  def addSkillOpenPoint(skillOpenPointNum: Int): PlayerDataType = this.copy(skillOpenPoint = skillOpenPoint + skillOpenPointNum)

  def removeSkillOpenPoint(skillOpenPointNum: Int): PlayerDataType = this.copy(skillOpenPoint = skillOpenPoint - skillOpenPointNum)

  def addOpenedSkills(effectSkills: EffectSkills): PlayerDataType = this.copy(openedSkills = openedSkills ++ Set(effectSkills))

  def addVoteNumber(addVoteNum: Int): PlayerDataType = this.copy(voteNumber = voteNumber + addVoteNum)

  def setReVoteNumber(reVoteNum: Int): PlayerDataType = this.copy(reVoteNumber = reVoteNum)

  def addSpecialSkillOpenPoint(addSpecialSkillOpenPoint: Int): PlayerDataType = this.copy(specialSkillOpenPoint = specialSkillOpenPoint + addSpecialSkillOpenPoint)

  def removeSpecialSkillOpenPoint(removeSpecialSkillOpenPoint: Int): PlayerDataType = this.copy(specialSkillOpenPoint = specialSkillOpenPoint - removeSpecialSkillOpenPoint)

  def addOpenedSpecialSkill(specialSkill: String): PlayerDataType = this.copy(openedSpecialSkills = openedSpecialSkills ++ Set(specialSkill))

  def addOpenedTitles(title: String): PlayerDataType = this.copy(openedTitles = openedTitles ++ Set(title))

  def removeOpenedTitles(title: String): PlayerDataType = this.copy(openedTitles = openedTitles.filterNot(_ == title))

  def setSelectedTitle(title: Option[String]): PlayerDataType = this.copy(selectedTitle = title)

  def toggleAutoStack(): PlayerDataType = this.copy(autoStack = !autoStack)

  def apply(implicit p: OfflinePlayer): Unit = new PlayerDataRepository().updateData(p.getUniqueId,this)

}
