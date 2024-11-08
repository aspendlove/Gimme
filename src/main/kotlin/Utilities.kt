import kotlinx.datetime.LocalDate

// Potential feature - custom date formatting
fun formatDate(date: LocalDate): String {
    return "${date.monthNumber}.${date.dayOfMonth}.${date.year}"
}

fun createIso8601String(
    year: Int,
    month: Int,
    day: Int,
    hour: Int = 0,
    minute: Int = 0,
    second: Int = 0,
    millisecond: Int = 0,
    utcOffset: Int = 0
): String {
    var utcOffsetString = "${"%02d".format(utcOffset)}:00"
    if (utcOffset >= 0) {
        utcOffsetString = "+$utcOffsetString"
    }

    return "$year-${"%02d".format(month)}-${"%02d".format(day)}" +
            "T${"%02d".format(hour)}:${"%02d".format(minute)}:${"%02d".format(second)}.${"%03d".format(millisecond)}" +
            utcOffsetString
}

fun newlineToBreak(input: String): String {
    return input.replace("\n", "<br/>")
}
