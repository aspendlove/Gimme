interface InvoiceBuilder {
    suspend fun build(filePath: String, invoiceName: String)
}
