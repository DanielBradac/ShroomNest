package cz.bradacd.shroomnest

interface PeriodicallyConfigurable {
    var waitPer: Int?
    var runPer: Int?
    var waitTime: Int
    var runTime: Int

    fun deviceOn(): Boolean
}

interface ManuallyConfigurable {
    fun deviceOn(): Boolean
}

data class HumiditySettings(
    var humidifierOn: Boolean,
    var humidityRange: ClosedFloatingPointRange<Float>,
    var mode: HumiditySettingsMode,
    override var waitPer: Int?,
    override var runPer: Int?,
    override var waitTime: Int,
    override var runTime: Int
): PeriodicallyConfigurable, ManuallyConfigurable {
    override fun deviceOn() = humidifierOn
}

data class VentilationSettings(
    var fanOn: Boolean,
    var mode: VentilationSettingsMode,
    override var waitPer: Int?,
    override var runPer: Int?,
    override var waitTime: Int,
    override var runTime: Int
): PeriodicallyConfigurable, ManuallyConfigurable {
    override fun deviceOn() = fanOn
}