import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page.PdfOptions
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.Margin

fun htmlToPdf(html: String): ByteArray {
    // playwright, context, and page all need to be closed
    Playwright.create().use{ playwright ->
        playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true)).newContext().use { context ->
            context.newPage().use { page ->
                page.setContent(html)
                return page.pdf(
                    PdfOptions()
                        .setMargin(
                            Margin()
                                .setLeft("0.5in")
                                .setRight("0.5in")
                                .setTop("0.5in")
                                .setBottom("0.5in")
                        )
                )
            }
        }
    }
}
