package components

class TestEntry(title: String, required: Boolean, singleLine: Boolean)
    : Entry<String>(title, required, singleLine) {
    override fun handleValueChanges(textValue: String): String {
        return textValue
    }
}
