package components

class TestEntry(title: String, required: Boolean, singleLine: Boolean)
    : Entry<String>(title, required, singleLine) {
    override fun handleValueChanges(textValue: String): Pair<String, String> {
        return Pair(textValue, textValue)
    }
}
