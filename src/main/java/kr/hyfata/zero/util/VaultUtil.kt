package kr.hyfata.zero.util

import kr.hyfata.zero.vault.VaultHook
import org.bukkit.OfflinePlayer

object VaultUtil {
    fun getBalance(target: OfflinePlayer?): Double {
        return if (VaultHook.hasEconomy()) VaultHook.getBalance(target) else 0.0
    }

    fun addBalance(target: OfflinePlayer?, amount: Double) {
        if (VaultHook.hasEconomy()) {
            VaultHook.deposit(target, amount)
        }
    }

    fun removeBalance(target: OfflinePlayer?, amount: Double) {
        if (VaultHook.hasEconomy()) {
            VaultHook.withdraw(target, amount)
        }
    }
}
