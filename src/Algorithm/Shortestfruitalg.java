package Algorithm;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import Coords.LatLonAlt;
import Geom.Circle;
import Geom.Path;
import Geom.Point3D;
import Packman_Game.Box;
import Packman_Game.Fruit;
import Packman_Game.Game;
import Packman_Game.Ghost;
import Packman_Game.Map;
import Packman_Game.Player;
import Robot.Packman;
import graph.Graph;
import graph.Node;

public class Shortestfruitalg {
	private Game game;
	//	private ArrayList<Box> tempboxs = new ArrayList<>();
	private double eps = 10;
	Map map=new Map();

	public Shortestfruitalg(Game g) {
		game = g;
		for (int i = 0; i < g.getBoxarr().size(); i++) {
			//			tempboxs.add(g.getBoxarr().get(i));
		}
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	private double Calculatetime(Player p, Fruit f) {
		Circle c = new Circle(p.getPos(), p.getRadius());
		Map m = new Map();
		double dist = m.distance3d(c.get_cen(), f.getPos()) - c.get_radius();
		if (dist <= 0)
			return 0;
		return dist / p.getSpeed();
	}

	private double Calculatetimewithbox(Player p, Fruit f) {
		Circle c = new Circle(p.getPos(), p.getRadius());
		Map map = new Map();
		double dist = 0;
		Path path = (calcpath(f, game));
		dist = path.GetDist();
		if (!path.getPoints().isEmpty())
			dist += map.distance3d(c.get_cen(), path.getPoints().get(0));
		return dist;
	}

	public Fruit shortpathalgo(Game game) {
		setGame(game);
		if (game.getBoxarr().isEmpty())
			return algowithoutboxes();
		return algowithboxs();
	}

	private Fruit algowithoutboxes() {
		double min = Double.MAX_VALUE;
		double tmp = 0;
		Fruit fruittemp = game.getFruitArr().get(0);
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			tmp = Calculatetime(game.getPlayer(), game.getFruitArr().get(i));
			if (tmp < min) {
				min = tmp;
				fruittemp = game.getFruitArr().get(i);
			}
		}
		return fruittemp;
	}

	private Fruit algowithboxs() {
		double min = Double.MAX_VALUE;
		double tmp = 0;
		Fruit fruittemp = game.getFruitArr().get(0);
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			tmp = Calculatetimewithbox(game.getPlayer(), game.getFruitArr().get(i));
			if (tmp < min) {
				min = tmp;
				fruittemp = game.getFruitArr().get(i);
			}
		}
		return fruittemp;
	}

	public boolean isIn(Point3D point) {
		boolean ans = true;
		double x = point.x();
		double y = point.y();
		for (Box box : game.getBoxarr()) {
			if (x > box.getRightUp().x() || x < box.getLeftDown().x() || y > box.getLeftDown().y()
					|| y < box.getRightUp().y()) {
				ans = false;
			}
		}
		return ans;
	}

	//	public ArrayList<Point3D> getOuters() {
	//		ArrayList<Point3D> ans = new ArrayList<Point3D>();
	//		for (Box box : game.getBoxarr()) {
	//			if (!isIn(box.getRightUp())) {
	//				box.getRightUp().set_y(box.getRightUp().y() + 0.000001);
	//				ans.add(box.getRightUp());
	//			} else if (!isIn(box.getLeftDown())) {
	//				box.getLeftDown().set_x(box.getLeftDown().x() - 0.000001);
	//				ans.add(box.getLeftDown());
	//			}
	//		}
	//		return ans;
	//	}
	//
	//	public ArrayList<Point3D> cleanShot(Point3D point) {
	//		ArrayList<Point3D> ans = new ArrayList<Point3D>();
	//		ArrayList<Point3D> outers = getOuters();
	//		for (int i = 0; i < outers.size(); i++) {
	//			if (!LineofSight(point, outers.get(i))) {
	//				ans.add(outers.get(i));
	//			}
	//
	//		}
	//		return ans;
	//	}
	//
	//	public ArrayList<Point3D> cleanShot() {
	//		Player player = game.getPlayer();
	//		ArrayList<Point3D> ans = new ArrayList<Point3D>();
	//		Point3D player_p = player.getPos();
	//		ArrayList<Point3D> outers = getOuters();
	//		for (int i = 0; i < outers.size(); i++) {
	//			if (!LineofSight(player_p, outers.get(i))) {
	//				ans.add(outers.get(i));
	//			}
	//		}
	//		return ans;
	//	}

	private Path calcpath(Fruit fruit, Game game) {
		Path p = new Path();
		if(LineofSight(game.getPlayer().getPos(), fruit.getPos())==true) {
			p.getPoints().add(fruit.getPos());
			return p;
		}
		ArrayList<Point3D> Points = new ArrayList<>();
		Points.add(game.getPlayer().getPos());
		Graph graph = new Graph();
		graph.add(new Node("player"));
		graph.add(new Node("fruit"));
		int count=0;
		for (Box box : game.getBoxarr()) {
			Point3D leftdown = box.getLeftDown();
			Point3D rightup = box.getRightUp();
			Point3D rightdown = new Point3D(rightup.ix(), leftdown.iy());
			Point3D leftup = new Point3D(leftdown.ix(), rightup.iy());
			graph.add(new Node("leftdown"));
			graph.add(new Node("rightup"));
			graph.add(new Node("rightdown"));
			graph.add(new Node("leftup"));
			graph.addEdge("leftdown", "rightdown",leftdown.distance2D(rightdown));
			graph.addEdge("leftdown", "leftup", leftdown.distance2D(leftup));
			graph.addEdge("rightup", "rightdown", rightup.distance2D(rightdown));
			graph.addEdge("rightup", "leftup", rightup.distance2D(leftup));
			Points.add(leftdown);
			Points.add(rightup);
			Points.add(rightdown);
			Points.add(leftup);
		}
		for (int i = 0; i < graph.size(); i++) {
		}
		return p;
	}

	public double escapefroomguest(Player p, Fruit f) {
		Map map = new Map();
		for (int i = 0; i < game.getGhostarr().size(); i++) {
			if (map.distance3d(p.getPos(), game.getGhostarr().get(i).getPos()) < 10)
				if (map.azimuth_elevation_dist(p.getPos(), f.getPos())[0] == map.azimuth_elevation_dist(p.getPos(),
						game.getGhostarr().get(i).getPos())[0]) {
					return searchangle(p, f, game.getGhostarr().get(i));
				}
		}
		return -1;
	}

	private double searchangle(Player p, Fruit f, Ghost g) {
		double Pangle = p.getAzimuth();
		double angle = Pangle;
		for (int i = 1; i <= 6; i++) {
			angle += 30 * i;
			if (move(f, angle) == true)
				return angle;
			angle = Pangle - (30 * i);
			if (move(f, angle) == true)
				return angle;
		}
		return -1;
	}

	private boolean escapeposcheck(Packman p, Fruit f) {
		Point3D tmp = p.getLocation();
		if (isIn(tmp) == true)
			return false;
		for (int i = 0; i < game.getGhostarr().size(); i++) {
			Point3D temp = p.getLocation();
			if (temp.equals(game.getGhostarr().get(i).getPos()))
				return false;
		}
		return true;
	}

	private boolean move(Fruit f, double angle) {
		Packman p = new Packman(new LatLonAlt(game.getPlayer().getPos().x(), game.getPlayer().getPos().y(), 0),
				game.getPlayer().getSpeed());
		p.setOrientation(angle);
		p.move(100.0D);
		if (escapeposcheck(p, f) == false)
			return false;
		return true;
	}

	public double Go2Fruit() {
		Map map = new Map();
		for (int i = 0; i < game.getFruitArr().size(); i++) {
			if (map.distance3d(game.getPlayer().getPos(), game.getFruitArr().get(i).getPos()) < 10)
				return map.azimuth_elevation_dist(game.getPlayer().getPos(), game.getFruitArr().get(i).getPos())[0];
		}
		return -1;
	}

	private boolean isColliding(Rectangle2D rect1, Line2D line2) {
		if (line2 != null) {
			return line2.intersects(rect1);
		}
		return false;
	}

	public boolean LineofSight(Point3D point1, Point3D point2) {
		// Map map = new Map();
		// Player player = new Player(game.getPlayer());
		Line2D line = new Line2D.Double(point1.x(), point1.y(), point2.x(), point2.y());
		ArrayList<Box> boxs = new ArrayList<>();
		for (int i = 0; i < game.getBoxarr().size(); i++) {
			boxs.add(new Box(game.getBoxarr().get(i)));
			double minx = Math.min(boxs.get(i).getLeftDown().x(), boxs.get(i).getRightUp().x());
			double miny = Math.min(boxs.get(i).getLeftDown().y(), boxs.get(i).getRightUp().y());
			double xwidth = Math.abs(boxs.get(i).getLeftDown().x() - boxs.get(i).getRightUp().x());
			double yhight = Math.abs(boxs.get(i).getLeftDown().y() - boxs.get(i).getRightUp().y());
			Rectangle2D r = new Rectangle2D.Double(minx, miny, xwidth, yhight);
			if (isColliding(r, line) == true)
				return false;
		}
		return true;
	}

	public boolean LineofSight(Point3D pstart, Point3D pend, int width, int hight) {
		Map map = new Map();
		pstart = new Point3D(game.getPlayer().getPos());
		pstart = map.CoordsToPixel(pstart, width, hight);
		pend = map.CoordsToPixel(pend, width, hight);
		Line2D line = new Line2D.Double(pstart.ix(), pstart.iy(), pend.ix(), pend.iy());
		ArrayList<Box> boxs = new ArrayList<>();
		for (int i = 0; i < game.getBoxarr().size(); i++) {
			boxs.add(new Box(game.getBoxarr().get(i)));
			boxs.get(i).setLeftDown(map.CoordsToPixel(boxs.get(i).getLeftDown(), width, hight));
			boxs.get(i).setRightUp(map.CoordsToPixel(boxs.get(i).getRightUp(), width, hight));
			int minx = Math.min(boxs.get(i).getLeftDown().ix(), boxs.get(i).getRightUp().ix());
			int miny = Math.min(boxs.get(i).getLeftDown().iy(), boxs.get(i).getRightUp().iy());
			int xwidth = Math.abs(boxs.get(i).getLeftDown().ix() - boxs.get(i).getRightUp().ix());
			int yhight = Math.abs(boxs.get(i).getLeftDown().iy() - boxs.get(i).getRightUp().iy());
			Rectangle2D r = new Rectangle2D.Double(minx, miny, xwidth, yhight);
			if (isColliding(r, line) == true)
				return false;
		}
		return true;
	}

	//	private void moveboxs() {
	//		Point3D tmp;
	//		Map map = new Map();
	//		for (int i = 0; i < tempboxs.size(); i++) {
	//			tmp = tempboxs.get(i).getLeftDown();
	//			tempboxs.get(i).setLeftDown(new Point3D(tmp.x() - eps, tmp.y()));
	//			tmp = tempboxs.get(i).getRightUp();
	//			tempboxs.get(i).setRightUp(new Point3D(tmp.x() + eps, tmp.y()));
	//		}
}

//	public static void main(String[] args) {
//		Map map = new Map();
//		Game game = new Game();
//		game = game.load("C:\\Users\\barge\\eclipse-workspace\\Ex4-OOP\\data\\cheacks.csv");
//		game.setPlayer(new Player(0, new Point3D(35.20614347700701, 32.10385349848943), 20, 1));
//		Point3D tmp = game.getPlayer().getPos();
//		tmp = map.CoordsToPixel(tmp, 200, 200);
//		game.getPlayer().setPos(tmp);
//		Shortestfruitalg alg = new Shortestfruitalg(game);
//		for (int i = 0; i < game.getBoxarr().size(); i++) {
//			if (alg.LineofSight(game.getPlayer().getPos(), game.getBoxarr().get(i).getLeftDown(), 200, 200) == true)
//				System.out.println("yay");
//			// System.out.println(game.getPlayer().getPos().toString()+" player.
//			// "+game.getBoxarr().get(i).getLeftDown()+" leftdown box.
//			// "+game.getBoxarr().get(i).getRightUp()+" rightupbox.");
//			if (alg.LineofSight(game.getPlayer().getPos(), game.getBoxarr().get(i).getRightUp(), 200, 200) == true)
//				System.out.println("yay2");
//		}
//		alg.moveboxs();
//		for (int i = 0; i < alg.tempboxs.size(); i++) {
//			// System.out.println(game.getPlayer().getPos().toString()+" player.
//			// "+alg.tempboxs.get(i).getLeftDown()+" leftdown box.
//			// "+alg.tempboxs.get(i).getRightUp()+" rightupbox.");
//			if (alg.LineofSight(game.getPlayer().getPos(), alg.tempboxs.get(i).getLeftDown(), 200, 200) == true)
//				System.out.println("yay");
//			if (alg.LineofSight(game.getPlayer().getPos(), alg.tempboxs.get(i).getRightUp(), 200, 200) == true)
//				System.out.println("yay2");
//		}
//		System.out.println("done");
//	}
//}