package kr.hyfata.zero.util;

import kr.hyfata.zero.vault.VaultHook;
import org.bukkit.OfflinePlayer;

public class VaultUtil {
    public static double getBalance(OfflinePlayer target) {
        return VaultHook.hasEconomy() ? VaultHook.getBalance(target) : 0;
    }

    public static void addBalance(OfflinePlayer target, double amount) {
        if (VaultHook.hasEconomy()) {
            VaultHook.deposit(target, amount);
        }
    }

    public static void removeBalance(OfflinePlayer target, double amount) {
        if (VaultHook.hasEconomy()) {
            VaultHook.withdraw(target, amount);
        }
    }
}
