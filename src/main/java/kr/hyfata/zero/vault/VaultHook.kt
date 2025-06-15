package kr.hyfata.zero.vault

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

object VaultHook {
    private var economy: Economy? = null

    private fun setupEconomy() {
        val rsp = Bukkit.getServicesManager().getRegistration(Economy::class.java)

        if (rsp != null) economy = rsp.getProvider()
    }

    fun hasEconomy(): Boolean {
        return economy != null
    }

    fun getBalance(target: OfflinePlayer?): Double {
        if (!hasEconomy()) throw UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.")

        return economy!!.getBalance(target)
    }

    fun withdraw(target: OfflinePlayer?, amount: Double): String? {
        if (!hasEconomy()) throw UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.")

        return economy!!.withdrawPlayer(target, amount).errorMessage
    }

    fun deposit(target: OfflinePlayer?, amount: Double): String? {
        if (!hasEconomy()) throw UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.")

        return economy!!.depositPlayer(target, amount).errorMessage
    }

    fun formatCurrencySymbol(amount: Double): String? {
        if (!hasEconomy()) throw UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.")

        return economy!!.format(amount)
        //return amount + " " + (((int) amount) == 1 ? economy.currencyNameSingular() : economy.currencyNamePlural());
    }

    init {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy()
        }
    }
}