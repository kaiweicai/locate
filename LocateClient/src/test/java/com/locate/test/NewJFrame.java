package com.locate.test;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

public class NewJFrame extends JFrame{
	  public static Map<Character,Integer> charmap = new HashMap<Character,Integer>();
	    public static char stringmap[] = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o',
	    'p','q' ,'r','s','t','u','v','w','x','y','z'
	    };
	    static{
	        charmap.put('a', 1);
	        charmap.put('b', 2);
	        charmap.put('c', 3);
	        charmap.put('d', 4);
	        charmap.put('e', 5);
	        charmap.put('f', 6);
	        charmap.put('g', 7);
	        charmap.put('h', 8);
	        charmap.put('i', 9);
	        charmap.put('j', 10);
	        charmap.put('k', 11);
	        charmap.put('l', 12);
	        charmap.put('m', 13);
	        charmap.put('n', 14);
	        charmap.put('o', 15);
	        charmap.put('p', 16);
	        charmap.put('q', 17);
	        charmap.put('r', 18);
	        charmap.put('s', 19);
	        charmap.put('t', 20);
	        charmap.put('u', 21);
	        charmap.put('v', 22);
	        charmap.put('w', 23);
	        charmap.put('x', 24);
	        charmap.put('y', 25);
	        charmap.put('z', 26);

	        charmap.put('A', 1);
	        charmap.put('B', 2);
	        charmap.put('C', 3);
	        charmap.put('D', 4);
	        charmap.put('E', 5);
	        charmap.put('F', 6);
	        charmap.put('G', 7);
	        charmap.put('H', 8);
	        charmap.put('I', 9);
	        charmap.put('J', 10);
	        charmap.put('K', 11);
	        charmap.put('L', 12);
	        charmap.put('M', 13);
	        charmap.put('N', 14);
	        charmap.put('O', 15);
	        charmap.put('P', 16);
	        charmap.put('Q', 17);
	        charmap.put('R', 18);
	        charmap.put('S', 19);
	        charmap.put('T', 20);
	        charmap.put('U', 21);
	        charmap.put('V', 22);
	        charmap.put('W', 23);
	        charmap.put('X', 24);
	        charmap.put('Y', 25);
	        charmap.put('Z', 26);
	    }
	    public NewJFrame() {
	        initComponents();
	    }

	    private char change(char c) {
	       int i = charmap.get(c);
	       int k = 2;
	       int j;
	       j=(i+k) % 26 ;
	       return stringmap[--j];
	    }

	    // <editor-fold defaultstate="collapsed" desc="Generated Code">
	    private void initComponents() {

	        jPanel1 = new javax.swing.JPanel();
	        ba = new javax.swing.JButton();
	        ta = new javax.swing.JTextField();
	        tb = new javax.swing.JTextField();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	        ba.setText("转换");
	        ba.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                baActionPerformed(evt);
	            }
	        });

	        tb.setEnabled(false);

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addGap(66, 66, 66)
	                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addComponent(tb)
	                            .addComponent(ta, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)))
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addGap(147, 147, 147)
	                        .addComponent(ba, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(59, Short.MAX_VALUE))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(ta, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(tb, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
	                .addGap(18, 18, 18)
	                .addComponent(ba)
	                .addContainerGap())
	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        );

	        pack();
	    }// </editor-fold>

	    private void baActionPerformed(java.awt.event.ActionEvent evt) {                                   
	      String aa = ta.getText();
	      char bb[] = aa.toCharArray();
	      char cc[] = new char[bb.length];
	      for(int i = 0; i < bb.length;i++){
	          cc[i] = change(bb[i]);
	      }
	      tb.setText(String.valueOf(cc));
	    }                                  

	    public static void main(String args[]) {
	        java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new NewJFrame().setVisible(true);
	            }
	        });
	    }

	    // Variables declaration - do not modify
	    private javax.swing.JButton ba;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JTextField ta;
	    private javax.swing.JTextField tb;
	    // End of variables declaration

}
