package project_cg.geometry.planeCartesians.cartesiansPlane.recortePlanes;

import project_cg.geometry.clipping.CohenSutherlandLineClipper;
import project_cg.geometry.planeCartesians.cartesiansPlane.CartesianPlane2D;
import project_cg.geometry.planeCartesians.cartesiansPlane.LineClippingPlane;
import project_cg.geometry.points.Point2D;
import project_cg.primitives.MidpointLine;
import utils.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecorteSutherlandPlane extends CartesianPlane2D implements LineClippingPlane {

	private static final int DEFAULT_VIEWPORT_WIDTH = 300;
	private static final int DEFAULT_VIEWPORT_HEIGHT = 220;
	private static final int DEFAULT_RANDOM_LINES = 12;

	private final List<LineSegment> originalLines;
	private final List<LineSegment> clippedLines;
	private final Random random;
	private boolean hideClippedSegments;

	private int viewportWidth;
	private int viewportHeight;
	private int xMin;
	private int yMin;
	private int xMax;
	private int yMax;

	public RecorteSutherlandPlane() {
		this.originalLines = new ArrayList<>();
		this.clippedLines = new ArrayList<>();
		this.random = new Random();
		this.hideClippedSegments = false;

		setViewportSize(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT);
		generateRandomLines(DEFAULT_RANDOM_LINES);
	}

	@Override
	public void setViewportSize(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("A viewport deve ter largura e altura positivas.");
		}

		this.viewportWidth = width;
		this.viewportHeight = height;

		this.xMin = 0;
		this.yMin = 0;
		this.xMax = width;
		this.yMax = height;

		this.clippedLines.clear();
		redrawScene();
	}

	@Override
	public void addCustomLine(Point2D start, Point2D end) {
		originalLines.add(new LineSegment(start, end));
		clippedLines.clear();
		redrawScene();
	}

	@Override
	public void generateRandomLines(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("A quantidade de linhas deve ser maior que zero.");
		}
		clippedLines.clear();

		int insideCount = Math.max(1, quantity / 2);
		int outsideCount = quantity - insideCount;

		for (int i = 0; i < insideCount; i++) {
			Point2D start = randomPointInsideViewport();
			Point2D end = randomPointInsideViewport();
			originalLines.add(new LineSegment(start, end));
		}

		for (int i = 0; i < outsideCount; i++) {
			originalLines.add(randomOutsideLine());
		}

		redrawScene();
	}

	@Override
	public void applyClipping() {
		clippedLines.clear();

		CohenSutherlandLineClipper clipper = new CohenSutherlandLineClipper(xMin, yMin, xMax, yMax);

		for (LineSegment segment : originalLines) {
			Point2D[] clipped = clipper.clipLine(
					(int) Math.round(segment.start.x),
					(int) Math.round(segment.start.y),
					(int) Math.round(segment.end.x),
					(int) Math.round(segment.end.y)
			);

			if (clipped != null) {
				clippedLines.add(new LineSegment(clipped[0], clipped[1]));
			}
		}

		redrawScene();
	}

	@Override
	public int getOriginalLineCount() {
		return originalLines.size();
	}

	@Override
	public int getClippedLineCount() {
		return clippedLines.size();
	}

	@Override
	public int getViewportWidthValue() {
		return viewportWidth;
	}

	@Override
	public int getViewportHeightValue() {
		return viewportHeight;
	}

	@Override
	public void setHideClippedSegments(boolean hideClippedSegments) {
		this.hideClippedSegments = hideClippedSegments;
		redrawScene();
	}

	@Override
	public boolean isHideClippedSegments() {
		return hideClippedSegments;
	}

	@Override
	public void clear() {
		originalLines.clear();
		clippedLines.clear();
		drawCartesianPlane();
		drawViewportWindowCenteredAtOrigin();
		repaint();
	}

	@Override
	public void drawCartesianPlane() {
		int width = image.getWidth();
		int height = image.getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, Constants.BACKGROUND_CARTESIAN_PLANE);
			}
		}

		repaint();
	}

	@Override
	public RecorteSutherlandPlane reset() {
		originalLines.clear();
		clippedLines.clear();
		setViewportSize(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT);
		generateRandomLines(DEFAULT_RANDOM_LINES);
		return this;
	}

	private void redrawScene() {
		drawCartesianPlane();
		drawViewportWindowCenteredAtOrigin();

		if (clippedLines.isEmpty()) {
			drawSegments(originalLines, Color.RED.getRGB());
		} else if (hideClippedSegments) {
			drawSegments(clippedLines, Color.RED.getRGB());
		} else {
			drawSegments(originalLines, Color.RED.getRGB());
			drawSegments(clippedLines, Color.GREEN.getRGB());
		}

		repaint();
	}

	private void drawViewportWindowCenteredAtOrigin() {
		int halfWidth = viewportWidth / 2;
		int halfHeight = viewportHeight / 2;

		Point2D topLeft = new Point2D(-halfWidth, halfHeight);
		Point2D topRight = new Point2D(halfWidth, halfHeight);
		Point2D bottomRight = new Point2D(halfWidth, -halfHeight);
		Point2D bottomLeft = new Point2D(-halfWidth, -halfHeight);

		int borderColor = Color.WHITE.getRGB();
		drawLineWithMidpointCentered(topLeft, topRight, borderColor);
		drawLineWithMidpointCentered(topRight, bottomRight, borderColor);
		drawLineWithMidpointCentered(bottomRight, bottomLeft, borderColor);
		drawLineWithMidpointCentered(bottomLeft, topLeft, borderColor);
	}

	private void drawSegments(List<LineSegment> segments, int rgb) {
		for (LineSegment segment : segments) {
			drawLineWithMidpointBottomLeft(segment.start, segment.end, rgb);
		}
	}

	private void drawLineWithMidpointCentered(Point2D start, Point2D end, int rgb) {
		MidpointLine midpointLine = new MidpointLine(point -> setPixel(point, rgb));
		midpointLine.desenhaLinha(start, end);
	}

	private void drawLineWithMidpointBottomLeft(Point2D start, Point2D end, int rgb) {
		MidpointLine midpointLine = new MidpointLine(point -> setPixelBottomLeft(point, rgb));
		midpointLine.desenhaLinha(start, end);
	}

	@Override
	public void setPixel(Point2D point, int rgb) {
		super.setPixel(point, rgb);
	}

	private void setPixelBottomLeft(Point2D point, int rgb) {
		int x = (int) Math.round(point.x);
		int y = (int) Math.round(point.y);

		int viewportOriginX = (image.getWidth() - viewportWidth) / 2;
		int viewportOriginY = (image.getHeight() - viewportHeight) / 2;

		int screenX = viewportOriginX + x;
		int screenY = viewportOriginY + (viewportHeight - 1 - y);

		if (screenX >= 0 && screenX < image.getWidth() && screenY >= 0 && screenY < image.getHeight()) {
			image.setRGB(screenX, screenY, rgb);
		}
	}

	private Point2D randomPointInsideViewport() {
		int x = randomBetween(xMin, xMax);
		int y = randomBetween(yMin, yMax);
		return new Point2D(x, y);
	}

	private LineSegment randomOutsideLine() {
		int rangeX = Math.max(80, viewportWidth * 2);
		int rangeY = Math.max(80, viewportHeight * 2);

		Point2D start = randomPointInExpandedRange(rangeX, rangeY);
		Point2D end = randomPointInExpandedRange(rangeX, rangeY);

		int tries = 0;
		while (isInsideViewport(start) && isInsideViewport(end) && tries < 20) {
			start = randomPointInExpandedRange(rangeX, rangeY);
			end = randomPointInExpandedRange(rangeX, rangeY);
			tries++;
		}

		return new LineSegment(start, end);
	}

	private Point2D randomPointInExpandedRange(int rangeX, int rangeY) {
		int x = randomBetween(-rangeX / 2, rangeX);
		int y = randomBetween(-rangeY / 2, rangeY);
		return new Point2D(x, y);
	}

	private boolean isInsideViewport(Point2D point) {
		return point.x >= xMin && point.x <= xMax && point.y >= yMin && point.y <= yMax;
	}

	private int randomBetween(int min, int max) {
		if (min == max) {
			return min;
		}

		return random.nextInt((max - min) + 1) + min;
	}

	private static class LineSegment {
		private final Point2D start;
		private final Point2D end;

		private LineSegment(Point2D start, Point2D end) {
			this.start = start;
			this.end = end;
		}
	}
}
