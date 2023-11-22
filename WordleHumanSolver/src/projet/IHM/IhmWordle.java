package projet.IHM;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import projet.Francais;
import projet.Main;
import saveData.Data;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.JTextArea;

public class IhmWordle extends JFrame {
	private String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private boolean running = false,
					guested = false;
	private String currentWord = "";
	private int ntry,nLetter=0;
	private JTable table;
	private JTextArea logger;

	public Data data;

	/**
	 * Create the application.
	 */
	public IhmWordle() {
		initialize();
	}
	public void keyPressed(KeyEvent e) {
        System.out.println("keyPressed");
    }
    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped");
    }
	public void keyReleased(KeyEvent e) {
        System.out.println("keyReleased");
		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
			backspace();
		else if (e.getKeyCode()==KeyEvent.VK_ENTER)
			enter();
		else if (((e.getKeyCode() > 64 && e.getKeyCode() < 91) || (e.getKeyCode() > 96 && e.getKeyCode() < 123))
			&& letters.contains(KeyEvent.getKeyText(e.getKeyCode()).toUpperCase()))
					addletter(KeyEvent.getKeyText(e.getKeyCode()).toUpperCase());
    }
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 450, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane();
		topPanel.add(splitPane);
		JButton startHuman = new JButton("startHuman");
		startHuman.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(running) {
					if(askBoolConfirm("restart?")) {
						try {
							Main.getMain().getGameTask().stop();
							Main.getMain().getGameTask().join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Main.getMain().mode = true;
						running = true;
						Main.getMain().resetGameTask().start();
					}
				} else {
					Main.getMain().mode = true;
					running=true;
					Main.getMain().getGameTask().start();
				}
			}
		});
		splitPane.setLeftComponent(startHuman);
		JButton startModele = new JButton("startModele");
		startModele.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(running) {
					if(askBoolConfirm("restart?")) {
						try {
							Main.getMain().getGameTask().stop();
							Main.getMain().getGameTask().join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						Main.getMain().mode = false;
						running = true;
						Main.getMain().resetGameTask().start();
					}
				} else {
					Main.getMain().mode = false;
					running=true;
					Main.getMain().getGameTask().start();
				}
			}
		});
		splitPane.setRightComponent(startModele);

		table = initTable();
		getContentPane().add(table, BorderLayout.CENTER);
		logger = new JTextArea("start");
		JScrollPane jsp= new JScrollPane(
                logger,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,    //La barre verticale toujours visible
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //La barre horizontale toujours visible
		jsp.setPreferredSize(new Dimension(200,150));
		add(jsp,BorderLayout.SOUTH);
	}
	private JTable initTable() {
		JTable temp = new JTable(5,5);
		temp.setFocusable(false);
		temp.setRowHeight(40);
		TableColumnModel columnModel = temp.getColumnModel();
		for(int i=0;i<5;i++) columnModel.getColumn(i).setWidth(40);
		return temp;
	}

	private void updateCurrentWord() {
		String temp = "";
		for(int i=0;i<nLetter;i++)
			temp += table.getModel().getValueAt(ntry, i);
		currentWord = temp;
	}
	
	protected void addletter(String keyText) {
		if(nLetter<5) {
			table.getModel().setValueAt(keyText,ntry, nLetter);
			nLetter++;			
		} else { log("deja 5 lettres!");}
	}

	protected void enter() {
		if(nLetter==5) {
			data.validateWord(currentWord);
			log(currentWord+"<==");
			ntry++;
			nLetter=0;
			guested=true;
		} else { log("il faut 5 lettres pour valider!");}
	}

	protected void backspace() {
		if(nLetter>0) {
			table.getModel().setValueAt("",ntry, nLetter-1);
			updateCurrentWord();
			nLetter--;
			if(nLetter>0) {
				data.addWord(currentWord);
				log(currentWord);
			}
		} else { log("aucune lettre à effacer!");}
	}

	private void log(String log) {
		logger.setText(logger.getText()+"\n"+log);
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
		while(!guested)	{
			try {
				Thread.sleep(1);
			} catch (InterruptedException ignored) {}
		}
		String temp = currentWord;
		updateCurrentWord();
		return temp;
	}

	public void setTry(int i) {
		ntry = i;
	}

	public void end(boolean win,String correctWord) {
		log(win?"Victoire!":("Raté, le mot était "+correctWord));
		data.end();
		running=false;
	}
}
