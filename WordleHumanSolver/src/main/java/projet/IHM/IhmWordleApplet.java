package projet.IHM;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import projet.Main;
import projet.Wordle;
import projet.saveData.Data;
import projet.saveData.DixData;

import javax.swing.JApplet;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JTextArea;

@SuppressWarnings("removal")
public class IhmWordleApplet extends JApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8441123529036685445L;
	public static String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public boolean running = false;
	private boolean guested = false;
	private int ntry = 0,nLetter=0;
	private JTable table;
	private JTextArea logger;

	public char[][][] tabGues;
	private String currentWord = "";
	public Data data;
	public DixData datas;
	/**
	 * Create the application.
	 */
	public IhmWordleApplet() { initialize(); }

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if(IhmWordleApplet.this.isFocusOwner() && running) {
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
						backspace();
					else if (e.getKeyCode()==KeyEvent.VK_ENTER)
						enter();
					else if (((e.getKeyCode() > 64 && e.getKeyCode() < 91) || (e.getKeyCode() > 96 && e.getKeyCode() < 123))
							&& letters.contains(KeyEvent.getKeyText(e.getKeyCode()).toUpperCase()))
						addletter(KeyEvent.getKeyText(e.getKeyCode()).toUpperCase().charAt(0));
				}
			}
			return false;
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("deprecation")
	private void initialize() {
		datas = new DixData();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new MyDispatcher());
		setBounds(100, 100, 450, 450);
		getRootPane().setLayout(new BorderLayout(0, 0));

		JPanel topPanel = new JPanel();
		getRootPane().add(topPanel, BorderLayout.NORTH);

		JButton startHuman = new JButton("startHuman");
		startHuman.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(running && askBoolConfirm("restart as humain?")) {
					try { 
						MainApplet.getMain().getGameTask().stop();
						MainApplet.getMain().getGameTask().join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} else if(running) return;
				MainApplet.getMain().mode = true;
				running = true;
				MainApplet.getMain().resetGameTask().start();
			}
		});
		topPanel.add(startHuman);
		
		initTable();
		table.setRowSelectionAllowed(false);
		table.repaint();
		
		getRootPane().add(table, BorderLayout.CENTER);
		logger = new JTextArea("start");
		JScrollPane jsp= new JScrollPane(
				logger,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,    //La barre verticale toujours visible
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //La barre horizontale toujours visible
		jsp.setPreferredSize(new Dimension(200,150));
		jsp.setAutoscrolls(true);
		getRootPane().add(jsp,BorderLayout.SOUTH);
	}
	private void initTable() {
		tabGues = new char[5][5][2];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				tabGues[i][j][0] = ' ';
				tabGues[i][j][1] = ' ';
			}
		}
		table = new JTable(5,5){
			/**
			 * 
			 */
			private static final long serialVersionUID = -3219292342230190237L;

			public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };;
		table.setFocusable(false);
		table.setOpaque(true);
		table.setDefaultRenderer(Object.class,new CustomRenderer());
		table.setRowHeight(40);
		TableColumnModel columnModel = table.getColumnModel();
		for(int i=0;i<5;i++) columnModel.getColumn(i).setWidth(40);
	}
	private void updateCurrentWord() {
		String temp = "";
		for(int i=0;i<nLetter;i++)
			temp += tabGues[ntry][i][0];
		currentWord = temp;
	}

	public void addletter(char keyText) {
		if(nLetter<5) {
			tabGues[ntry][nLetter][0] = keyText;
			nLetter++;
			updateCurrentWord();
			data.addWord(currentWord);
			log(currentWord);
		} else { log("deja 5 lettres!");}
		validate();
		repaint();
	}

	protected void enter() {
		if(nLetter==5) {
			log(currentWord+" gues");
			guested=true;
		} else { log("il faut 5 lettres pour valider!");}
	}

	protected void backspace() {
		if(nLetter>0) {
			tabGues[ntry][nLetter-1][0] = ' ';
			nLetter--;
			updateCurrentWord();
			if(nLetter>0) {
				data.addWord(currentWord);
				log(currentWord);
			}
		} else { log("aucune lettre à effacer!");}
		validate();
		repaint();
	}

	public void log(String log) {
		logger.setText(logger.getText()+"\n"+log);
	    logger.setCaretPosition(logger.getDocument().getLength());
	}

	public void clear() {
		data = new Data(Main.getMain().mode);
		initTable();
	}

	protected boolean askBoolConfirm(String txt) {
		return JOptionPane.showConfirmDialog(this,txt, "Check",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION?true:false;
	}

	public String getGues() {
		updateCurrentWord();
		while(!guested)	{
			try {
				Thread.sleep(1);
			} catch (InterruptedException ignored) {}
		}
		guested = false;
		String temp = currentWord;
		System.out.println("guess : " + temp);
		return temp;
	}

	public void setTry(int i) {
		ntry = i;
		nLetter = 0;
	}
	public void end(boolean win,String correctWord) {
		log(win?"Victoire!":("Perdu, le mot était "+correctWord));
		data.setEnd();
		running=false;
	}
	public class CustomRenderer extends JLabel implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5355456270385935883L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(tabGues[row][column][0]+"");
			setOpaque(true);
			this.setHorizontalAlignment(JLabel.CENTER);
			Character c = tabGues[row][column][1];
			if(c.equals(Wordle.DEFAULT)) setBackground(Color.LIGHT_GRAY);
			else if(c.equals(Wordle.VERT)) setBackground(Color.GREEN);
			else if(c.equals(Wordle.JAUNE)) setBackground(Color.YELLOW);
			else if(c.equals(Wordle.GRIS)) setBackground(Color.GRAY);
			else setBackground(Color.DARK_GRAY);
			return this;
		}
	}
	public void resetRow(String w) {
		log("mot inconnu : "+w);
		for (int j = 0; j < 5; j++) {
			tabGues[ntry][j][0] = ' ';
		}
		nLetter = 0;
		validate();
		repaint();
	}
}
