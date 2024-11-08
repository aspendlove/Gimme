
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import storage.Client
import storage.Item
import storage.StateBundle
import storage.User
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class HtmlBuilder : InvoiceBuilder {
    override suspend fun build(filePath: String, invoiceName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fullPrice = StateBundle.total
            val htmlString = generateString(
                StateBundle.user,
                StateBundle.client,
                invoiceName,
                StateBundle.notes.note,
                fullPrice.toString(),
                formatRows(StateBundle.items)
            )
            val file = File(filePath)
            if (!file.createNewFile()) {
                file.delete()
                file.createNewFile()
            }
            val fileWriter = FileWriter(filePath)
            fileWriter.write(htmlString)
            fileWriter.close()
            val desktop = Desktop.getDesktop()
            desktop.open(file)
        }
    }

    private fun generateString(
        user: User,
        client: Client,
        invoiceName: String,
        notes: String,
        total: String,
        rows: String
    ): String {
        val cal: Calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("MM.dd.yyyy")
        val invoiceDate = formatter.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 30)
        val dueDate = formatter.format(cal.time)

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Invoice</title>
            <style>
            #root {
            margin: 0;
            width: 100%;
            min-height: 100%;
            }
    
            body {
                font-family: 'Roboto', sans-serif;
            }
    
            #logo {
                font-size: 2em;
                font-weight: bold;
            }
            
            #notes {
                max-width: 60%;
            }
            
            #total {
                margin-right: 1.5em;
            }
    
            #info ul {
                text-align: right;
                list-style: none;
            }
    
            #name {
                font-size:1.5em;
            }
    
            #items {
                margin-top: 3em;
            }
    
            #items table {
                min-width: 100%;
                text-align: left;
            }
    
            #items table tr th {
                margin-left: 1em;
            }
    
            #bottom-box {
                margin-top: 2em;
            }
    
            .double-box {
                display:flex;
                flex-direction: row;
                flex-wrap: nowrap;
                align-items:center;
                padding: 1em;
                justify-content: space-between;
            }
    
            a {
                color: black;
                text-decoration-line: none;
            }
    
            .accent {
                color: #066093;
            }
            </style>
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link href="https://fonts.googleapis.com/css2?family=Roboto&family=Roboto+Slab&display=swap" rel="stylesheet">
        </head>
        <body>
    
        <div id="root">
            <div class="double-box" id="top-box">
                <div id="logo" class="accent">${user.businessName}</div>
                <div id="info">
                    <ul>
                        <li id="name">${user.contactName}</li>
                        <p>${newlineToBreak(user.subtitle)}</p>
                        <li><br/></li>
                        <li>${user.street}</li>
                        <li>${user.city}, ${user.state} ${user.zip}</li>
                        ${formatOptionals(user.email, user.phone)}
                    </ul>
                </div>
            </div>
            <div class="double-box" id="mid-box">
                <div id="client">
                    <p style="font-size: 1.5em" class="accent">Bill to:</p>
                    <p style="font-size: 1.2em">${client.businessName}</p>
                    ${client.contactName} <br/>
                    ${client.street} <br/>
                    ${client.city}, ${client.state} ${client.zip} <br/>
                    ${formatOptionals(client.email, client.phone)}
                </div>
                <div id="invoice">
                    <p> <span style="font-weight: bold">Invoice Number:</span> $invoiceName<br/>
                        <span style="font-weight: bold">Invoice Date:</span> $invoiceDate<br/>
                        <span style="font-weight: bold">Due date:</span> $dueDate</p>
                </div>
            </div>
            <div id="items">
                <table>
                    <tr>
                        <th style="width: 2em" class="accent">#</th>
                        <th style="width: 8em" class="accent">Date(s)</th>
                        <th class="accent">Description</th>
                        <th style="width: 6em" class="accent">Quantity</th>
                        <th style="width: 6em" class="accent">Price</th>
                        <th style="width: 6em" class="accent">Item Total</th>
                    </tr>
                    $rows
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td><span style="font-weight: bolder;  font-size: larger;" class="accent">Total</span><br/></td>
                        <td>$$total</td>
                    </tr>
                </table>
            </div>
            <div class="double-box" id="bottom-box">
                <div id="notes">
                    <p>
                        <span style="font-weight: bold;" class="accent">Notes</span><br/>
                        $notes
                    </p>
                </div>
            </div>
        </div>
    
        </body>
        </html>
    """.trimIndent()
    }

    private fun formatRows(items: List<Item>): String {
        val workingString: StringBuilder = StringBuilder()
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        for ((iteration, item: Item) in items.withIndex()) {
            val date = if (item.endDate != null) {
                "${simpleDateFormat.format(item.startDate)} to\n${simpleDateFormat.format(item.endDate)}"
            } else {
                simpleDateFormat.format(item.startDate)
            }

            val formattedDescription: String = newlineToBreak(item.description)
            workingString.append(
                """
        <tr>
            <td>${iteration + 1}</td>
            <td>
                <p>
                    $date
                </p>
            </td>
            <td>
                <p>
                    <span>${item.name}</span> <br>
                    $formattedDescription
                </p>
            </td>
            <td>${item.quantity}</td>
            <td>$${item.price}</td>
            <td>$${item.total}</td>
        </tr>
        """
            )
        }

        return workingString.toString()
    }

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

}
