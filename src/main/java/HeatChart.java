/*
 *  Copyright 2010 Tom Castle (www.tc33.org)
 *  Licensed under GNU Lesser General Public License
 *
 *  This file is part of JHeatChart - the heat maps charting api for Java.
 *
 *  JHeatChart is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JHeatChart is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JHeatChart.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.*;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Math.*;
import static javax.imageio.ImageIO.getImageWritersByFormatName;
import static javax.imageio.ImageIO.write;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

public class HeatChart {

    public static final double SCALE_LOGARITHMIC = 0.3;

    public static final double SCALE_LINEAR = 1.0;

    public static final double SCALE_EXPONENTIAL = 3;

    // x, y, z data values.
    private double[][] zValues;
    private Object[] xValues;
    private Object[] yValues;

    private boolean xValuesHorizontal;
    private boolean yValuesHorizontal;

    // General chart settings.
    private Dimension cellSize;
    private Dimension chartSize;
    private int margin;
    private Color backgroundColour;

    // Title settings.
    private String title;
    private Font titleFont;
    private Color titleColour;
    private Dimension titleSize;
    private int titleAscent;

    // Axis settings.
    private int axisThickness;
    private Color axisColour;
    private Font axisLabelsFont;
    private Color axisLabelColour;
    private String xAxisLabel;
    private String yAxisLabel;
    private Color axisValuesColour;
    private Font axisValuesFont; // The font size will be considered the maximum font size - it may be smaller if needed to fit in.
    private int xAxisValuesFrequency;
    private int yAxisValuesFrequency;
    private boolean showXAxisValues;
    private boolean showYAxisValues;

    // Generated axis properties.
    private int xAxisValuesHeight;
    private int xAxisValuesWidthMax;

    private int yAxisValuesHeight;
    private int yAxisValuesAscent;
    private int yAxisValuesWidthMax;

    private Dimension xAxisLabelSize;
    private int xAxisLabelDescent;

    private Dimension yAxisLabelSize;
    private int yAxisLabelAscent;

    // Heat map colour settings.
    private Color highValueColour;
    private Color lowValueColour;

    // How many RGB steps there are between the high and low colours.
    private int colourValueDistance;

    private double lowValue;
    private double highValue;

    // Key co-ordinate positions.
    private Point heatMapTL;
    private Point heatMapBR;
    private Point heatMapC;

    // Heat map dimensions.
    private Dimension heatMapSize;

    // Control variable for mapping z-values to colours.
    private double colourScale;

    public HeatChart(double[][] zValues) {
        this(zValues, min(zValues), max(zValues));
    }

    public HeatChart(double[][] zValues, double low, double high) {
        this.zValues = zValues;
        this.lowValue = low;
        this.highValue = high;

        // Default x/y-value settings.
        setXValues(0, 1);
        setYValues(0, 1);

        // Default chart settings.
        this.cellSize = new Dimension(20, 20);
        this.margin = 20;
        this.backgroundColour = WHITE;

        // Default title settings.
        this.title = null;
        this.titleFont = new Font("Sans-Serif", BOLD, 16);
        this.titleColour = BLACK;

        // Default axis settings.
        this.xAxisLabel = null;
        this.yAxisLabel = null;
        this.axisThickness = 2;
        this.axisColour = BLACK;
        this.axisLabelsFont = new Font("Sans-Serif", PLAIN, 12);
        this.axisLabelColour = BLACK;
        this.axisValuesColour = BLACK;
        this.axisValuesFont = new Font("Sans-Serif", PLAIN, 10);
        this.xAxisValuesFrequency = 1;
        this.xAxisValuesHeight = 0;
        this.xValuesHorizontal = false;
        this.showXAxisValues = true;
        this.showYAxisValues = true;
        this.yAxisValuesFrequency = 1;
        this.yAxisValuesHeight = 0;
        this.yValuesHorizontal = true;

        // Default heatmap settings.
        this.highValueColour = BLACK;
        this.lowValueColour = WHITE;
        this.colourScale = SCALE_LINEAR;

        updateColourDistance();
    }

    public double getLowValue() {
        return lowValue;
    }

    public double getHighValue() {
        return highValue;
    }

    public double[][] getZValues() {
        return zValues;
    }

    public void setZValues(double[][] zValues) {
        setZValues(zValues, min(zValues), max(zValues));
    }

    public void setZValues(double[][] zValues, double low, double high) {
        this.zValues = zValues;
        this.lowValue = low;
        this.highValue = high;
    }

    public void setXValues(double xOffset, double xInterval) {
        // Update the x-values according to the offset and interval.
        xValues = new Object[zValues[0].length];
        for (int i = 0; i < zValues[0].length; i++) {
            xValues[i] = xOffset + (i * xInterval);
        }
    }

    public void setXValues(Object[] xValues) {
        this.xValues = xValues;
    }

    public void setYValues(double yOffset, double yInterval) {
        // Update the y-values according to the offset and interval.
        yValues = new Object[zValues.length];
        for (int i = 0; i < zValues.length; i++) {
            yValues[i] = yOffset + (i * yInterval);
        }
    }

    public void setYValues(Object[] yValues) {
        this.yValues = yValues;
    }

    public Object[] getXValues() {
        return xValues;
    }

    public Object[] getYValues() {
        return yValues;
    }

    public void setXValuesHorizontal(boolean xValuesHorizontal) {
        this.xValuesHorizontal = xValuesHorizontal;
    }

    public boolean isXValuesHorizontal() {
        return xValuesHorizontal;
    }

    public void setYValuesHorizontal(boolean yValuesHorizontal) {
        this.yValuesHorizontal = yValuesHorizontal;
    }

    public boolean isYValuesHorizontal() {
        return yValuesHorizontal;
    }

    @Deprecated
    public void setCellWidth(int cellWidth) {
        setCellSize(new Dimension(cellWidth, cellSize.height));
    }

    @Deprecated
    public int getCellWidth() {
        return cellSize.width;
    }

    @Deprecated
    public void setCellHeight(int cellHeight) {
        setCellSize(new Dimension(cellSize.width, cellHeight));
    }

    @Deprecated
    public int getCellHeight() {
        return cellSize.height;
    }

    public void setCellSize(Dimension cellSize) {
        this.cellSize = cellSize;
    }

    public Dimension getCellSize() {
        return cellSize;
    }

    @Deprecated
    public int getChartWidth() {
        return chartSize.width;
    }

    @Deprecated
    public int getChartHeight() {
        return chartSize.height;
    }

    public Dimension getChartSize() {
        return chartSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public int getChartMargin() {
        return margin;
    }

    public void setChartMargin(int margin) {
        this.margin = margin;
    }

    public Color getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(Color backgroundColour) {
        if (backgroundColour == null) {
            backgroundColour = WHITE;
        }

        this.backgroundColour = backgroundColour;
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    public Color getTitleColour() {
        return titleColour;
    }

    public void setTitleColour(Color titleColour) {
        this.titleColour = titleColour;
    }

    public int getAxisThickness() {
        return axisThickness;
    }

    public void setAxisThickness(int axisThickness) {
        this.axisThickness = axisThickness;
    }

    public Color getAxisColour() {
        return axisColour;
    }

    public void setAxisColour(Color axisColour) {
        this.axisColour = axisColour;
    }

    public Font getAxisLabelsFont() {
        return axisLabelsFont;
    }

    public void setAxisLabelsFont(Font axisLabelsFont) {
        this.axisLabelsFont = axisLabelsFont;
    }

    public Color getAxisLabelColour() {
        return axisLabelColour;
    }

    public void setAxisLabelColour(Color axisLabelColour) {
        this.axisLabelColour = axisLabelColour;
    }

    public Font getAxisValuesFont() {
        return axisValuesFont;
    }

    public void setAxisValuesFont(Font axisValuesFont) {
        this.axisValuesFont = axisValuesFont;
    }

    public Color getAxisValuesColour() {
        return axisValuesColour;
    }

    public void setAxisValuesColour(Color axisValuesColour) {
        this.axisValuesColour = axisValuesColour;
    }

    public int getXAxisValuesFrequency() {
        return xAxisValuesFrequency;
    }

    public void setXAxisValuesFrequency(int axisValuesFrequency) {
        this.xAxisValuesFrequency = axisValuesFrequency;
    }

    public int getYAxisValuesFrequency() {
        return yAxisValuesFrequency;
    }

    public void setYAxisValuesFrequency(int axisValuesFrequency) {
        yAxisValuesFrequency = axisValuesFrequency;
    }

    public boolean isShowXAxisValues() {
        //TODO Could get rid of these flags and use a frequency of -1 to signal no values.
        return showXAxisValues;
    }

    public void setShowXAxisValues(boolean showXAxisValues) {
        this.showXAxisValues = showXAxisValues;
    }

    public boolean isShowYAxisValues() {
        return showYAxisValues;
    }

    public void setShowYAxisValues(boolean showYAxisValues) {
        this.showYAxisValues = showYAxisValues;
    }

    public Color getHighValueColour() {
        return highValueColour;
    }

    public void setHighValueColour(Color highValueColour) {
        this.highValueColour = highValueColour;

        updateColourDistance();
    }

    public Color getLowValueColour() {
        return lowValueColour;
    }

    public void setLowValueColour(Color lowValueColour) {
        this.lowValueColour = lowValueColour;

        updateColourDistance();
    }

    public double getColourScale() {
        return colourScale;
    }

    public void setColourScale(double colourScale) {
        this.colourScale = colourScale;
    }

    /*
     * Calculate and update the field for the distance between the low colour
     * and high colour. The distance is the number of steps between one colour
     * and the other using an RGB coding with 0-255 values for each of red,
     * green and blue. So the maximum colour distance is 255 + 255 + 255.
     */
    private void updateColourDistance() {
        int r1 = lowValueColour.getRed();
        int g1 = lowValueColour.getGreen();
        int b1 = lowValueColour.getBlue();
        int r2 = highValueColour.getRed();
        int g2 = highValueColour.getGreen();
        int b2 = highValueColour.getBlue();

        colourValueDistance = abs(r1 - r2);
        colourValueDistance += abs(g1 - g2);
        colourValueDistance += abs(b1 - b2);
    }


    public void saveToFile(File outputFile) throws IOException {
        String filename = outputFile.getName();

        int extPoint = filename.lastIndexOf('.');

        if (extPoint < 0) {
            throw new IOException("Illegal filename, no extension used.");
        }

        // Determine the extension of the filename.
        String ext = filename.substring(extPoint + 1);

        // Handle jpg without transparency.
        if (ext.toLowerCase().equals("jpg") || ext.toLowerCase().equals("jpeg")) {
            BufferedImage chart = (BufferedImage) getChartImage(false);

            // Save our graphic.
            saveGraphicJpeg(chart, outputFile, 1.0f);
        } else {
            BufferedImage chart = (BufferedImage) getChartImage(true);

            write(chart, ext, outputFile);
        }
    }

    private void saveGraphicJpeg(BufferedImage chart, File outputFile, float quality) throws IOException {
        // Setup correct compression for jpeg.
        Iterator<ImageWriter> iter = getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);

        // Output the image.
        FileImageOutputStream output = new FileImageOutputStream(outputFile);
        writer.setOutput(output);
        IIOImage image = new IIOImage(chart, null, null);
        writer.write(null, image, iwp);
        writer.dispose();

    }


    public Image getChartImage(boolean alpha) {
        // Calculate all unknown dimensions.
        measureComponents();
        updateCoordinates();

        // Determine image type based upon whether require alpha or not.
        // Using BufferedImage.TYPE_INT_ARGB seems to break on jpg.
        int imageType = (alpha ? TYPE_4BYTE_ABGR : TYPE_3BYTE_BGR);

        // Create our chart image which we will eventually draw everything on.
        BufferedImage chartImage = new BufferedImage(chartSize.width, chartSize.height, imageType);
        Graphics2D chartGraphics = chartImage.createGraphics();

        // Use anti-aliasing where ever possible.
        chartGraphics.setRenderingHint(KEY_ANTIALIASING,
                VALUE_ANTIALIAS_ON);

        // Set the background.
        chartGraphics.setColor(backgroundColour);
        chartGraphics.fillRect(0, 0, chartSize.width, chartSize.height);

        // Draw the title.
        drawTitle(chartGraphics);

        // Draw the heatmap image.
        drawHeatMap(chartGraphics, zValues);

        // Draw the axis labels.
        drawXLabel(chartGraphics);
        drawYLabel(chartGraphics);

        // Draw the axis bars.
        drawAxisBars(chartGraphics);

        // Draw axis values.
        drawXValues(chartGraphics);
        drawYValues(chartGraphics);

        return chartImage;
    }


    public Image getChartImage() {
        return getChartImage(false);
    }

    /*
     * Calculates all unknown component dimensions.
     */
    private void measureComponents() {
        //TODO This would be a good place to check that all settings have sensible values or throw illegal state exception.

        //TODO Put this somewhere so it only gets created once.
        BufferedImage chartImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
        Graphics2D tempGraphics = chartImage.createGraphics();

        // Calculate title dimensions.
        if (title != null) {
            tempGraphics.setFont(titleFont);
            FontMetrics metrics = tempGraphics.getFontMetrics();
            titleSize = new Dimension(metrics.stringWidth(title), metrics.getHeight());
            titleAscent = metrics.getAscent();
        } else {
            titleSize = new Dimension(0, 0);
        }

        // Calculate x-axis label dimensions.
        if (xAxisLabel != null) {
            tempGraphics.setFont(axisLabelsFont);
            FontMetrics metrics = tempGraphics.getFontMetrics();
            xAxisLabelSize = new Dimension(metrics.stringWidth(xAxisLabel), metrics.getHeight());
            xAxisLabelDescent = metrics.getDescent();
        } else {
            xAxisLabelSize = new Dimension(0, 0);
        }

        // Calculate y-axis label dimensions.
        if (yAxisLabel != null) {
            tempGraphics.setFont(axisLabelsFont);
            FontMetrics metrics = tempGraphics.getFontMetrics();
            yAxisLabelSize = new Dimension(metrics.stringWidth(yAxisLabel), metrics.getHeight());
            yAxisLabelAscent = metrics.getAscent();
        } else {
            yAxisLabelSize = new Dimension(0, 0);
        }

        // Calculate x-axis value dimensions.
        if (showXAxisValues) {
            tempGraphics.setFont(axisValuesFont);
            FontMetrics metrics = tempGraphics.getFontMetrics();
            xAxisValuesHeight = metrics.getHeight();
            xAxisValuesWidthMax = 0;
            for (Object o : xValues) {
                int w = metrics.stringWidth(o.toString());
                if (w > xAxisValuesWidthMax) {
                    xAxisValuesWidthMax = w;
                }
            }
        } else {
            xAxisValuesHeight = 0;
        }

        // Calculate y-axis value dimensions.
        if (showYAxisValues) {
            tempGraphics.setFont(axisValuesFont);
            FontMetrics metrics = tempGraphics.getFontMetrics();
            yAxisValuesHeight = metrics.getHeight();
            yAxisValuesAscent = metrics.getAscent();
            yAxisValuesWidthMax = 0;
            for (Object o : yValues) {
                int w = metrics.stringWidth(o.toString());
                if (w > yAxisValuesWidthMax) {
                    yAxisValuesWidthMax = w;
                }
            }
        } else {
            yAxisValuesHeight = 0;
        }

        // Calculate heatmap dimensions.
        int heatMapWidth = (zValues[0].length * cellSize.width);
        int heatMapHeight = (zValues.length * cellSize.height);
        heatMapSize = new Dimension(heatMapWidth, heatMapHeight);

        int yValuesHorizontalSize = 0;
        if (yValuesHorizontal) {
            yValuesHorizontalSize = yAxisValuesWidthMax;
        } else {
            yValuesHorizontalSize = yAxisValuesHeight;
        }

        int xValuesVerticalSize = 0;
        if (xValuesHorizontal) {
            xValuesVerticalSize = xAxisValuesHeight;
        } else {
            xValuesVerticalSize = xAxisValuesWidthMax;
        }

        // Calculate chart dimensions.
        int chartWidth = heatMapWidth + (2 * margin) + yAxisLabelSize.height + yValuesHorizontalSize + axisThickness;
        int chartHeight = heatMapHeight + (2 * margin) + xAxisLabelSize.height + xValuesVerticalSize + titleSize.height + axisThickness;
        chartSize = new Dimension(chartWidth, chartHeight);
    }

    /*
     * Calculates the co-ordinates of some key positions.
     */
    private void updateCoordinates() {
        // Top-left of heat map.
        int x = margin + axisThickness + yAxisLabelSize.height;
        x += (yValuesHorizontal ? yAxisValuesWidthMax : yAxisValuesHeight);
        int y = titleSize.height + margin;
        heatMapTL = new Point(x, y);

        // Top-right of heat map.
        x = heatMapTL.x + heatMapSize.width;
        y = heatMapTL.y + heatMapSize.height;
        heatMapBR = new Point(x, y);

        // Centre of heat map.
        x = heatMapTL.x + (heatMapSize.width / 2);
        y = heatMapTL.y + (heatMapSize.height / 2);
        heatMapC = new Point(x, y);
    }

    /*
     * Draws the title String on the chart if title is not null.
     */
    private void drawTitle(Graphics2D chartGraphics) {
        if (title != null) {
            // Strings are drawn from the baseline position of the leftmost char.
            int yTitle = (margin / 2) + titleAscent;
            int xTitle = (chartSize.width / 2) - (titleSize.width / 2);

            chartGraphics.setFont(titleFont);
            chartGraphics.setColor(titleColour);
            chartGraphics.drawString(title, xTitle, yTitle);
        }
    }

    /*
     * Creates the actual heatmap element as an image, that can then be drawn
     * onto a chart.
     */
    private void drawHeatMap(Graphics2D chartGraphics, double[][] data) {
        // Calculate the available size for the heatmap.
        int noYCells = data.length;
        int noXCells = data[0].length;

        //double dataMin = min(data);
        //double dataMax = max(data);

        BufferedImage heatMapImage = new BufferedImage(heatMapSize.width, heatMapSize.height, TYPE_INT_ARGB);
        Graphics2D heatMapGraphics = heatMapImage.createGraphics();

        for (int x = 0; x < noXCells; x++) {
            for (int y = 0; y < noYCells; y++) {
                // Set colour depending on zValues.
                heatMapGraphics.setColor(getCellColour(data[y][x], lowValue, highValue));

                int cellX = x * cellSize.width;
                int cellY = y * cellSize.height;

                heatMapGraphics.fillRect(cellX, cellY, cellSize.width, cellSize.height);
            }
        }

        // Draw the heat map onto the chart.
        chartGraphics.drawImage(heatMapImage, heatMapTL.x, heatMapTL.y, heatMapSize.width, heatMapSize.height, null);
    }

    /*
     * Draws the x-axis label string if it is not null.
     */
    private void drawXLabel(Graphics2D chartGraphics) {
        if (xAxisLabel != null) {
            // Strings are drawn from the baseline position of the leftmost char.
            int yPosXAxisLabel = chartSize.height - (margin / 2) - xAxisLabelDescent;
            //TODO This will need to be updated if the y-axis values/label can be moved to the right.
            int xPosXAxisLabel = heatMapC.x - (xAxisLabelSize.width / 2);

            chartGraphics.setFont(axisLabelsFont);
            chartGraphics.setColor(axisLabelColour);
            chartGraphics.drawString(xAxisLabel, xPosXAxisLabel, yPosXAxisLabel);
        }
    }

    /*
     * Draws the y-axis label string if it is not null.
     */
    private void drawYLabel(Graphics2D chartGraphics) {
        if (yAxisLabel != null) {
            // Strings are drawn from the baseline position of the leftmost char.
            int yPosYAxisLabel = heatMapC.y + (yAxisLabelSize.width / 2);
            int xPosYAxisLabel = (margin / 2) + yAxisLabelAscent;

            chartGraphics.setFont(axisLabelsFont);
            chartGraphics.setColor(axisLabelColour);

            // Create 270 degree rotated transform.
            AffineTransform transform = chartGraphics.getTransform();
            AffineTransform originalTransform = (AffineTransform) transform.clone();
            transform.rotate(toRadians(270), xPosYAxisLabel, yPosYAxisLabel);
            chartGraphics.setTransform(transform);

            // Draw string.
            chartGraphics.drawString(yAxisLabel, xPosYAxisLabel, yPosYAxisLabel);

            // Revert to original transform before rotation.
            chartGraphics.setTransform(originalTransform);
        }
    }

    /*
     * Draws the bars of the x-axis and y-axis.
     */
    private void drawAxisBars(Graphics2D chartGraphics) {
        if (axisThickness > 0) {
            chartGraphics.setColor(axisColour);

            // Draw x-axis.
            int x = heatMapTL.x - axisThickness;
            int y = heatMapBR.y;
            int width = heatMapSize.width + axisThickness;
            int height = axisThickness;
            chartGraphics.fillRect(x, y, width, height);

            // Draw y-axis.
            x = heatMapTL.x - axisThickness;
            y = heatMapTL.y;
            width = axisThickness;
            height = heatMapSize.height;
            chartGraphics.fillRect(x, y, width, height);
        }
    }

    /*
     * Draws the x-values onto the x-axis if showXAxisValues is set to true.
     */
    private void drawXValues(Graphics2D chartGraphics) {
        if (!showXAxisValues) {
            return;
        }

        chartGraphics.setColor(axisValuesColour);

        for (int i = 0; i < xValues.length; i++) {
            if (i % xAxisValuesFrequency != 0) {
                continue;
            }

            String xValueStr = xValues[i].toString();

            chartGraphics.setFont(axisValuesFont);
            FontMetrics metrics = chartGraphics.getFontMetrics();

            int valueWidth = metrics.stringWidth(xValueStr);

            if (xValuesHorizontal) {
                // Draw the value with whatever font is now set.
                int valueXPos = (i * cellSize.width) + ((cellSize.width / 2) - (valueWidth / 2));
                valueXPos += heatMapTL.x;
                int valueYPos = heatMapBR.y + metrics.getAscent() + 1;

                chartGraphics.drawString(xValueStr, valueXPos, valueYPos);
            } else {
                int valueXPos = heatMapTL.x + (i * cellSize.width) + ((cellSize.width / 2) + (xAxisValuesHeight / 2));
                int valueYPos = heatMapBR.y + axisThickness + valueWidth;

                // Create 270 degree rotated transform.
                AffineTransform transform = chartGraphics.getTransform();
                AffineTransform originalTransform = (AffineTransform) transform.clone();
                transform.rotate(toRadians(270), valueXPos, valueYPos);
                chartGraphics.setTransform(transform);

                // Draw the string.
                chartGraphics.drawString(xValueStr, valueXPos, valueYPos);

                // Revert to original transform before rotation.
                chartGraphics.setTransform(originalTransform);
            }
        }
    }

    /*
     * Draws the y-values onto the y-axis if showYAxisValues is set to true.
     */
    private void drawYValues(Graphics2D chartGraphics) {
        if (!showYAxisValues) {
            return;
        }

        chartGraphics.setColor(axisValuesColour);

        for (int i = 0; i < yValues.length; i++) {
            if (i % yAxisValuesFrequency != 0) {
                continue;
            }

            String yValueStr = yValues[i].toString();

            chartGraphics.setFont(axisValuesFont);
            FontMetrics metrics = chartGraphics.getFontMetrics();

            int valueWidth = metrics.stringWidth(yValueStr);

            if (yValuesHorizontal) {
                // Draw the value with whatever font is now set.
                int valueXPos = margin + yAxisLabelSize.height + (yAxisValuesWidthMax - valueWidth);
                int valueYPos = heatMapTL.y + (i * cellSize.height) + (cellSize.height / 2) + (yAxisValuesAscent / 2);

                chartGraphics.drawString(yValueStr, valueXPos, valueYPos);
            } else {
                int valueXPos = margin + yAxisLabelSize.height + yAxisValuesAscent;
                int valueYPos = heatMapTL.y + (i * cellSize.height) + (cellSize.height / 2) + (valueWidth / 2);

                // Create 270 degree rotated transform.
                AffineTransform transform = chartGraphics.getTransform();
                AffineTransform originalTransform = (AffineTransform) transform.clone();
                transform.rotate(toRadians(270), valueXPos, valueYPos);
                chartGraphics.setTransform(transform);

                // Draw the string.
                chartGraphics.drawString(yValueStr, valueXPos, valueYPos);

                // Revert to original transform before rotation.
                chartGraphics.setTransform(originalTransform);
            }
        }
    }

    /*
     * Determines what colour a heat map cell should be based upon the cell
     * values.
     */
    private Color getCellColour(double data, double min, double max) {
        double range = max - min;
        double position = data - min;

        // What proportion of the way through the possible values is that.
        double percentPosition = position / range;

        // Which colour group does that put us in.
        int colourPosition = getColourPosition(percentPosition);

        int r = lowValueColour.getRed();
        int g = lowValueColour.getGreen();
        int b = lowValueColour.getBlue();

        // Make n shifts of the colour, where n is the colourPosition.
        for (int i = 0; i < colourPosition; i++) {
            int rDistance = r - highValueColour.getRed();
            int gDistance = g - highValueColour.getGreen();
            int bDistance = b - highValueColour.getBlue();

            if ((abs(rDistance) >= abs(gDistance))
                    && (abs(rDistance) >= abs(bDistance))) {
                // Red must be the largest.
                r = changeColourValue(r, rDistance);
            } else if (abs(gDistance) >= abs(bDistance)) {
                // Green must be the largest.
                g = changeColourValue(g, gDistance);
            } else {
                // Blue must be the largest.
                b = changeColourValue(b, bDistance);
            }
        }

        return new Color(r, g, b);
    }

    /*
     * Returns how many colour shifts are required from the lowValueColour to
     * get to the correct colour position. The result will be different
     * depending on the colour scale used: LINEAR, LOGARITHMIC, EXPONENTIAL.
     */
    private int getColourPosition(double percentPosition) {
        return (int) round(colourValueDistance * pow(percentPosition, colourScale));
    }

    private int changeColourValue(int colourValue, int colourDistance) {
        if (colourDistance < 0) {
            return colourValue + 1;
        } else if (colourDistance > 0) {
            return colourValue - 1;
        } else {
            // This shouldn't actually happen here.
            return colourValue;
        }
    }

    public static double max(double[][] values) {
        double max = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                max = (values[i][j] > max) ? values[i][j] : max;
            }
        }
        return max;
    }


    public static double min(double[][] values) {
        double min = MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                min = (values[i][j] < min) ? values[i][j] : min;
            }
        }
        return min;
    }

}

