
import com.aspendlove.gimme.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import storage.Item
import storage.StateBundle
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class RegexBuilder : InvoiceBuilder {
    fun formatOptionals(email: String, phone: String): String {
        val workingOutput = StringBuilder()
        if (email.isNotEmpty()) {
            workingOutput.append("<a href=\"mailto:$email\">$email</a>")
            if (phone.isNotEmpty()) {
                workingOutput.append("\n")
            }
        }
        if (phone.isNotEmpty()) {
            workingOutput.append(phone)
        }
        return newlineToBreak(workingOutput.toString())
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun build(filePath: String, invoiceName: String) {

        val itemRows: StringBuilder = StringBuilder()
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
//        val resourcesDir = File(Res.getUri("files"))

        for ((iteration, item: Item) in StateBundle.items.withIndex()) {
            val date = if (item.endDate != null) {
                "${simpleDateFormat.format(item.startDate)} to\n${simpleDateFormat.format(item.endDate)}"
            } else {
                simpleDateFormat.format(item.startDate)
            }

            val data = mapOf(
                "Item" to mapOf(
                    "iteration" to iteration + 1,
                    "date" to date,
                    "name" to item.name,
                    "description" to item.description,
                    "quantity" to item.quantity,
                    "price" to item.price,
                    "total" to item.total,
                ),
            )
            itemRows.append(
                renderTemplate(
                    Res.readBytes("files/templates/default_item.html").decodeToString(),
                    data,
                    ::newlineToBreak
                )
            )
        }
        val cal: Calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("MM.dd.yyyy")
        val invoiceDate = formatter.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 30)
        val dueDate = formatter.format(cal.time)
        with(StateBundle) {
            val data = mapOf(
                "User" to mapOf(
                    "businessName" to user.businessName,
                    "contactName" to user.contactName,
                    "subtitle" to user.subtitle,
                    "street" to user.street,
                    "city" to user.city,
                    "state" to user.state,
                    "zip" to user.zip,
                    "optionals" to formatOptionals(user.email, user.phone),
                ),
                "Client" to mapOf(
                    "businessName" to client.businessName,
                    "contactName" to client.contactName,
                    "street" to client.street,
                    "city" to client.city,
                    "state" to client.state,
                    "zip" to client.zip,
                    "optionals" to formatOptionals(client.email, client.phone)
                ),
                "Invoice" to mapOf(
                    "notes" to notes.note,
                    "name" to invoiceName,
                    "total" to total,
                    "items" to itemRows,
                    "invoiceDate" to invoiceDate,
                    "dueDate" to dueDate,
                )
            )
            val result = renderTemplate(
                Res.readBytes("files/templates/default.html").decodeToString(),
                data,
                ::newlineToBreak
            )
            val file = File(filePath)
            if (!file.createNewFile()) {
                file.delete()
                file.createNewFile()
            }
            val fileWriter = FileOutputStream(file)
            fileWriter.write(htmlToPdf(result))
            fileWriter.close()
            val desktop = Desktop.getDesktop()
            desktop.open(file)
        }
    }

    fun renderTemplate(template: String, data: Map<String, Map<String, Any>>, normalize: (String) -> String): String {

        val regex = Regex("""\{\{(\w+)\.(\w+)}}""")

        return regex.replace(template) { matchResult ->
            val category = matchResult.groups[1]?.value
            val key = matchResult.groups[2]?.value

            normalize(data[category]?.get(key)?.toString() ?: matchResult.value)
        }
    }
}