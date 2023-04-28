package model


enum class ResultDiffEnum(desc: String, val status: String) {
    Keep("保持", "👏"),
    Decrease("减少", "👏"),
    Growth("增长", "⬆️"),
    ExceededThreshold("超出阈值", "❌"),
    UnKnown("未知", "👀")
}