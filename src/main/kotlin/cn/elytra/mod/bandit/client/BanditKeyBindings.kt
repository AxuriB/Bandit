package cn.elytra.mod.bandit.client

import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

object BanditKeyBindings {

	val trigger = KeyBinding("keybinding.bandit.trigger", Keyboard.KEY_NONE, "keybinding.bandit.category")
	val changeMode = KeyBinding("keybinding.bandit.change-mode", Keyboard.KEY_NONE, "keybinding.bandit.category")

}