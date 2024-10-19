package cz.bradacd.shroomnest

enum class VentilationSettingsMode(val code: String) {
    Periodic("period"),
    Manual("manual")
}

enum class HumiditySettingsMode(val code: String) {
    Automatic("auto"),
    Periodic("period"),
    Manual("manual")
}