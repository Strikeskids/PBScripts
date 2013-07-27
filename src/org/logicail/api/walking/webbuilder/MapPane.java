package org.logicail.api.walking.webbuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Manner
 * Date: 6/19/13
 * Time: 12:06 AM
 */
public class MapPane extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static final ArrayList<MapTile> UNDOED = new ArrayList<>();
	public static final ArrayList<MapTile> TILES = new ArrayList<>();
	public static boolean areaSelected = false;
	public static int dragSpacing = 5;
	boolean left = false;
	boolean right = false;
	Point mouse = new Point(0, 0);
	Point pathCursor = new Point(0, 0);
	MapTile start = new MapTile(-1, -1);
	private BufferedImage map = null;
	private Point mapLoc = new Point(-1547, -1605);
	private double scale = 1.0;
	private Label status = new Label("Initializing...");

	public MapPane() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setLayout(new BorderLayout());
		status.setAlignment(Label.CENTER);
		add(status, BorderLayout.CENTER);
		setVisible(true);
	}

	public void paint(Graphics g2) {
		Controls.setTile(getTile());
		Graphics2D g = (Graphics2D) g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (map != null) {
			g.scale(scale, scale);
			g.drawImage(map, mapLoc.x, mapLoc.y, null);
			getTile().draw(g, mapLoc, Color.white);

			g.scale(1 / scale, 1 / scale);
			if (areaSelected) {
				g.setColor(Color.white);
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f));
				Polygon area = new Polygon();
				if (TILES.size() == 2) {
					int x1 = TILES.get(0).getScaledGX(mapLoc, scale);
					int y1 = TILES.get(0).getScaledGY(mapLoc, scale);
					int x2 = TILES.get(1).getScaledGX(mapLoc, scale);
					int y2 = TILES.get(1).getScaledGY(mapLoc, scale);
					area.addPoint(x1, y1);
					area.addPoint(x2, y1);
					area.addPoint(x2, y2);
					area.addPoint(x1, y2);
				} else {
					for (MapTile t : TILES) {
						area.addPoint(t.getScaledGX(mapLoc, scale), t.getScaledGY(mapLoc, scale));
					}
				}
				g.fill(area);
				g.setColor(Color.black);
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1.0f));
			} else {
				for (int i = 1; i < TILES.size(); i++) {
					MapTile.drawLineBetween(g, mapLoc, scale, TILES.get(i - 1), TILES.get(i));
				}
			}
			for (MapTile t : TILES) {
				t.drawInScale(g, mapLoc, Color.red, scale);
			}
		}
	}

	public MapTile getTile() {
		return new MapTile((int) Math.round(2047.5 + ((-mapLoc.x + (mouse.x / scale)) / 2)), (int) Math.round(4159 - ((-mapLoc.y + (mouse.y / scale)) / 2)));
	}

	public void moveMap(int xDev, int yDev) {
		mapLoc.x = mapLoc.x - xDev;
		mapLoc.y = mapLoc.y - yDev;
	}

	public void scale(double change) {
		double newScale = scale + change * scale;
		if (newScale <= 10.1 && newScale >= .2) {
			scale = newScale;
		} else {
			change = 0;
		}
		double halfChange = Math.abs(change / 2);
		moveMap((int) (((change > 0) ? (getWidth() * halfChange) : (-getWidth() * halfChange)) / scale),
				(int) (((change > 0) ? (getHeight() * halfChange) : (-getHeight() * halfChange)) / scale));
		getParent().repaint();
	}

	public void getMap() {
		try {
			status.setText("Connect to runescape.com...");
			BufferedReader read = new BufferedReader(new InputStreamReader(
					new URL("http://www.runescape.com/downloads.ws").openStream()));
			String line;
			while ((line = read.readLine()) != null) {
				status.setText("Parsing to runescape.com...");
				if (line.contains("http://www.runescape.com/img/main/downloads_and_media/downloads_and_wallpapers/rs_map/")) {
					Pattern pattern = Pattern.compile("\\w{4}:(/|\\w|\\.|-)+");
					Matcher matcher = pattern.matcher(line);
					if (matcher.find())
						line = matcher.group();
					else {
						status.setText("Error getting map!");
					}
					status.setText("Loading Map...");
					map = ImageLoader.loadImage(line.substring(line.lastIndexOf('/') + 1), line);
					remove(status);
					getParent().repaint();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (contains(e.getPoint())) {
			GUI.showCode(false);
		}

		switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				left = true;
				if (e.getPoint().x >= 0 && e.getPoint().x <= getWidth() && e.getPoint().y >= 0 && e.getPoint().y <= getHeight()) {
					if (TILES.isEmpty()) UNDOED.clear();
					if (TILES.isEmpty() || !TILES.get(TILES.size() - 1).equals(getTile())) TILES.add(getTile());
				}
				break;
			case MouseEvent.BUTTON3:
				((Component) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				right = true;
				start = getTile();
				break;
		}

		if (contains(e.getPoint())) {
			getParent().repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				left = false;
				break;
			case MouseEvent.BUTTON3:
				((Component) e.getSource()).setCursor(Cursor.getDefaultCursor());
				right = false;
				break;
		}
		getParent().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (contains(e.getPoint())) {
			if (right) {
				MapTile tile = getTile();
				moveMap(start.getGX(mapLoc) - tile.getGX(mapLoc), start.getGY(mapLoc) - tile.getGY(mapLoc));
			}
			if (left) {
				if (e.getPoint().distance(pathCursor) >= (dragSpacing * 2 * scale)) {
					pathCursor = e.getPoint();
					if (TILES.isEmpty() || !TILES.get(TILES.size() - 1).equals(getTile()))
						TILES.add(getTile());
				}
			} else {
				pathCursor = e.getPoint();
			}
			mouse = e.getPoint();
			getParent().repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (contains(e.getPoint())) {
			mouse = e.getPoint();
			pathCursor = e.getPoint();
			getParent().repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		if (Math.abs(rotation) == 1) {
			scale(rotation * -0.1);
		}
	}
}
