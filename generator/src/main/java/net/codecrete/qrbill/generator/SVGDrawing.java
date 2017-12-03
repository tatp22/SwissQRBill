//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;

public class SVGDrawing implements GraphicsPort {

    private ByteArrayOutputStream buffer;
    private Writer stream;
    private boolean isInGroup = false;

    public SVGDrawing() throws IOException {
        buffer = new ByteArrayOutputStream();
        stream = new OutputStreamWriter(buffer, StandardCharsets.UTF_8);
        stream.write(
                "<?xml version=\"1.0\" standalone=\"no\"?>\r\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\r\n" +
                "<svg width=\"46mm\" height=\"46mm\" version=\"1.1\" viewBox=\"0 0 46 46\" xmlns=\"http://www.w3.org/2000/svg\">\r\n");
    }

    public void close() throws IOException {
        if (isInGroup) {
            stream.write("</g>\r\n");
            isInGroup = false;
        }
        if (stream != null) {
            stream.write("</svg>\r\n");
            stream.close();
            stream = null;
        }
    }

    public void startPath() throws IOException {
        stream.write("<path d=\"");
    }

    public void addRectangle(double x, double y, double width, double height) throws IOException {
        stream.write("M");
        stream.write(formatCoordinate(x));
        stream.write(",");
        stream.write(formatCoordinate(y));
        stream.write("h");
        stream.write(formatCoordinate(width));
        stream.write("v");
        stream.write(formatCoordinate(height));
        stream.write("h");
        stream.write(formatCoordinate(-width));
        stream.write("z ");
    }

    public void fillPath(int color) throws IOException {
        stream.write("\" fill=\"#");
        stream.write(formatColor(color));
        stream.write("\"/>\r\n");
    }

    public void setTransformation(double translateX, double translateY, double scale) throws IOException {
        if (isInGroup) {
            stream.write("</g>\r\n");
            isInGroup = false;
        }
        if (translateX != 0 || translateY != 0 || scale != 1) {
            stream.write("<g transform=\"translate(");
            stream.write(formatCoordinate(translateX));
            stream.write(" ");
            stream.write(formatCoordinate(translateY));
            if (scale != 1) {
                stream.write(") scale(");
                stream.write(formatCoordinate(scale));
            }
            stream.write(")\">\r\n");
            isInGroup = true;
        }
    }

    public byte[] getResult() throws IOException {
        close();
        return buffer.toByteArray();
    }

    private static final Locale TECH_LOCALE = Locale.US;
    private static final DecimalFormat COORDINATE_FORMAT = new DecimalFormat("#.###");

    private static String formatCoordinate(double value) {
        return COORDINATE_FORMAT.format(value);
    }

    private static String formatColor(int color) {
        return String.format(Locale.US, "%06x", color);
    }
}
