package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener
{
	public static FlappyBird flappyBird;
	private final int WIDTH = 800, HEIGHT = 800;
	private Renderer renderer;
	private Rectangle bird;
	private int ticks, yMotion, score, highScore;
	private ArrayList<Rectangle> columns;
	private Random rand;
	private boolean gameOver, started;
	
	public FlappyBird() //constructor
	{
		JFrame jframe = new JFrame(); //creates window for the program
		Timer timer = new Timer(20,this); //executes actionPerformed(...) every 20 ms
		renderer = new Renderer(); //creates renderer
		rand = new Random();
		bird = new Rectangle(WIDTH/2 - 10, HEIGHT/2 - 10, 20, 20); //creates bird with (location x, location y, width, height)
		columns = new ArrayList<Rectangle>(); //creates an ArrayList to store generated columns
		highScore = 0;
		
		jframe.add(renderer); //adds renderer
		jframe.setSize(WIDTH, HEIGHT); //sets the dimensions of the window
		jframe.setResizable(false); //allows user to be unable to resize window
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ends program when window is closed
		jframe.addMouseListener(this); //adds a MouseListener
		jframe.setTitle("Flappy Bird"); //title of the window
		jframe.setVisible(true); //displays the window
		
		addColumn(true); //creates the first 4 columns (2 upper, 2 lower)
		addColumn(true); //
		addColumn(true); //
		addColumn(true); //
		
		timer.start(); //starts the timer, i.e, begin updating every 20 ms
	}
	
	public void addColumn(boolean started) //adds objects of type Rectangle to the ArrayList 'columns'
	{
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);
		
		if(started)
		{
			columns.add(new Rectangle(WIDTH + width + columns.size()*300, HEIGHT - height - 150, width, height)); //technical measurements for position of column
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1)*300, 0, width, HEIGHT - height - space)); //
		}
		else
		{
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 150, width, height)); //
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space)); //
		}
	}
	
	public void paintColumn(Graphics g, Rectangle column) //paints a column
	{
		g.setColor(Color.green.darker()); //sets pen color to dark green
		g.fillRect(column.x, column.y, column.width, column.height); //fills a rectangle 
	}
	
	public void jump() //starts game or causes bird to jump - happens on mouse click
	{
		if(gameOver) //then restart the game by...
		{
			bird = new Rectangle(WIDTH/2 - 10, HEIGHT/2 - 10, 20, 20); //creating a new bird
			columns.clear(); //clearing the ArrayList columns
			yMotion = 0; //setting bird motion to 0
			score = 0; //resetting score
			
			addColumn(true); //adding first 4 columns
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;
		}
		
		if(!started)
		{
			started = true;
		}
		
		else if(!gameOver)
		{
			if(yMotion > 0)
			{
				yMotion = 0; 
			}
			yMotion -=10;
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) //actions performed every 20 ms
	{
		int speed = 10;
		ticks++;
		
		if(started)
		{
			for(int i=0; i < columns.size(); i++)
			{
				Rectangle column = columns.get(i);
				column.x -= speed;
			}
			
			if(ticks % 2 == 0 && yMotion < 15 && started)
			{
				yMotion += 2;
			}
			
			for(int i=0; i<columns.size(); i++)
			{
				Rectangle column = columns.get(i);
				if(column.x + column.width < 0)
				{
					columns.remove(column);
					if(column.y == 0)
					{
						addColumn(false);
					}
				}
			}
		}
		
		bird.y += yMotion;
		
		for(Rectangle column : columns)
		{
			if(column.y == 0 && bird.x + bird.width > column.x + column.width / 2 - 10 && bird.x + bird.width < column.x + column.width / 2 + 10)
			{
				score++;
			}
			if(column.intersects(bird))
			{
				gameOver = true;
				if(bird.x <= column.x)
				{
					bird.x = column.x - bird.width;
				}
				else
				{
					if(column.y != 0)
					{
						bird.y = column.y - bird.height;
					}
					else if(bird.y < column.height)
					{
						bird.y = column.height;
					}
				}
			}
		}
		
		if(bird.y  > HEIGHT - 150 || bird.y <=0)
		{
			gameOver = true;
		}
		
		if(bird.y + yMotion >= HEIGHT - 150)
		{
			bird.y = HEIGHT - 150 - bird.height;
			gameOver = true;
		}
		renderer.repaint();
	}
	
	
	public void repaint(Graphics g) //paints the program
	{
		
		g.setColor(Color.cyan); //paints cyan sky
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.yellow); //paints yellow sky
		g.fillOval(WIDTH / 4, HEIGHT / 4, 100, 100);
		
		g.setColor(Color.orange); //paints orange ground
		g.fillRect(0, HEIGHT - 150, WIDTH, 150);
		
		g.setColor(Color.green); //paints green grass
		g.fillRect(0, HEIGHT - 150, WIDTH, 20);
		
		g.setColor(Color.red); //paints red bird
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		
		if(ticks % 1000 >= 500) //changes background to night time every 5 seconds
		{
			g.setColor(Color.black); //paints dark sky
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			g.setColor(Color.yellow); //paints yellow moon
			g.fillOval(WIDTH / 4, HEIGHT / 4, 100, 100);
			g.setColor(Color.black);
			g.fillOval(WIDTH / 4 - 30,  HEIGHT / 4, 100, 100);
			
			g.setColor(Color.orange.darker().darker()); //paints double dark orange ground
			g.fillRect(0, HEIGHT - 150, WIDTH, 150);
			
			g.setColor(Color.green.darker().darker()); //paints double dark green grass
			g.fillRect(0, HEIGHT - 150, WIDTH, 20);
			
			g.setColor(Color.red.darker().darker()); //paints double dark red bird
			g.fillRect(bird.x, bird.y, bird.width, bird.height);
		}
		
		for(Rectangle column : columns)
		{
			paintColumn(g, column);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 100));
		
		if(!started)
		{
			g.drawString("Click to start!", 75, HEIGHT/2);
		}
		if(gameOver)
		{
			g.drawString("Game Over!", 75, HEIGHT/2 - 200);
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
			if(score > highScore)
			{
				highScore = score;
			}
			g.drawString("High Score: " + String.valueOf(highScore), WIDTH / 2 - 350, HEIGHT/2);
		}
		if(!gameOver && started)
		{
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) //calls jump() for every click
	{
		jump();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) //ignore
	{
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) //ignore
	{
	}
	
	@Override
	public void mousePressed(MouseEvent e) //ignore
	{
	}
	
	@Override
	public void mouseReleased(MouseEvent e) //ignore
	{
	}
	
	public static void main(String[] args) //main method
	{
		flappyBird = new FlappyBird();
	}

}
