package com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser;

import java.util.Hashtable;

import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. EscPosPrinterCommands;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. barcode.Barcode;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. barcode.Barcode128;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. barcode.BarcodeEAN13;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.barcode.BarcodeEAN8;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. barcode.BarcodeUPCA;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. barcode.BarcodeUPCE;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParserColumn;

public class PrinterTextParserBarcode implements com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. textparser.IPrinterTextParserElement {

    private Barcode barcode;
    private int length;
    private byte[] align;

    public PrinterTextParserBarcode(PrinterTextParserColumn printerTextParserColumn,
                                    String textAlign,
                                    Hashtable<String, String> barcodeAttributes,
                                    String code) throws EscPosParserException, EscPosBarcodeException {

        EscPosPrinter printer = printerTextParserColumn.getLine().getTextParser().getPrinter();
        code = code.trim();

        this.align = EscPosPrinterCommands.TEXT_ALIGN_LEFT;
        switch (textAlign) {
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.TAGS_ALIGN_CENTER:
                this.align = EscPosPrinterCommands.TEXT_ALIGN_CENTER;
                break;
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.TAGS_ALIGN_RIGHT:
                this.align = EscPosPrinterCommands.TEXT_ALIGN_RIGHT;
                break;
        }

        this.length = printer.getPrinterNbrCharactersPerLine();
        float height = 10f;

        if (barcodeAttributes.containsKey(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_HEIGHT)) {
            String barCodeAttribute = barcodeAttributes.get(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_HEIGHT);

            if (barCodeAttribute == null) {
                throw new EscPosParserException("Invalid barcode attribute: " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_HEIGHT);
            }

            try {
                height = Float.parseFloat(barCodeAttribute);
            } catch (NumberFormatException nfe) {
                throw new EscPosParserException("Invalid barcode " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_HEIGHT + " value");
            }
        }

        float width = 0f;
        if (barcodeAttributes.containsKey(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_WIDTH)) {
            String barCodeAttribute = barcodeAttributes.get(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_WIDTH);

            if (barCodeAttribute == null) {
                throw new EscPosParserException("Invalid barcode attribute: " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_WIDTH);
            }

            try {
                width = Float.parseFloat(barCodeAttribute);
            } catch (NumberFormatException nfe) {
                throw new EscPosParserException("Invalid barcode " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_WIDTH + " value");
            }
        }

        int textPosition = EscPosPrinterCommands.BARCODE_TEXT_POSITION_BELOW;
        if (barcodeAttributes.containsKey(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TEXT_POSITION)) {
            String barCodeAttribute = barcodeAttributes.get(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TEXT_POSITION);

            if (barCodeAttribute == null) {
                throw new EscPosParserException("Invalid barcode attribute: " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TEXT_POSITION);
            }

            switch (barCodeAttribute) {
                case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_NONE:
                    textPosition = EscPosPrinterCommands.BARCODE_TEXT_POSITION_NONE;
                    break;
                case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_ABOVE:
                    textPosition = EscPosPrinterCommands.BARCODE_TEXT_POSITION_ABOVE;
                    break;
            }
        }

        String barcodeType = com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_EAN13;

        if (barcodeAttributes.containsKey(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE)) {
            barcodeType = barcodeAttributes.get(com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE);

            if (barcodeType == null) {
                throw new EscPosParserException("Invalid barcode attribute : " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE);
            }
        }

        switch (barcodeType) {
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_EAN8:
                this.barcode = new BarcodeEAN8(printer, code, width, height, textPosition);
                break;
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_EAN13:
                this.barcode = new BarcodeEAN13(printer, code, width, height, textPosition);
                break;
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_UPCA:
                this.barcode = new BarcodeUPCA(printer, code, width, height, textPosition);
                break;
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_UPCE:
                this.barcode = new BarcodeUPCE(printer, code, width, height, textPosition);
                break;
            case com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter.textparser.PrinterTextParser.ATTR_BARCODE_TYPE_128:
                this.barcode = new Barcode128(printer, code, width, height, textPosition);
                break;
            default:
                throw new EscPosParserException("Invalid barcode attribute : " + com.dantsu.escposprinter.src.main.java.com.dantsu.escposprinter. textparser.PrinterTextParser.ATTR_BARCODE_TYPE);
        }
    }

    /**
     * Get the barcode width in char length.
     *
     * @return int
     */
    @Override
    public int length() throws EscPosEncodingException {
        return this.length;
    }

    /**
     * Print barcode
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    @Override
    public PrinterTextParserBarcode print(EscPosPrinterCommands printerSocket) {
        printerSocket
                .setAlign(this.align)
                .printBarcode(this.barcode);
        return this;
    }
}
