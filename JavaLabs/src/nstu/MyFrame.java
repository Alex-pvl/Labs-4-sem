package nstu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyFrame extends JFrame {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dimension = toolkit.getScreenSize();
    boolean willShowTime = false;
    boolean isStarted = false;
    Habitat h = new Habitat();
    long time;
    Timer timer;
    JPanel panel;
    JLabel timeLabel;
    JButton start;
    JButton stop;
    JCheckBox showInfo;
    JRadioButton showTimer;
    JRadioButton hideTimer;
    JOptionPane pane;
    JButton submitCar;
    JButton submitMoto;
    JTextField carsFreqText;
    JTextField motoFreqText;
    JComboBox<String> carProbability;
    JList<String> motoProbability;

    public void startSimulation() {
        if (!isStarted) {
            start.setEnabled(false);
            stop.setEnabled(true);
            start.setContentAreaFilled(false);
            stop.setContentAreaFilled(true);
            System.out.println("---------------------------");
            System.out.println("Car: chance - " + h.P1 + "%, time - " + h.N1 +
                    "\nBike: chance - " + h.P2 + "%, time - " + h.N2);
            repaint();

            isStarted = true;
            h.carCount = 0;
            h.motoCount = 0;
            timer = new Timer();
            time = 0;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    h.update(time);
                    add(new JComponent() {
                    });
                    time++;
                    timeLabel.setText("Время: " + time + " с");
                    repaint();
                }
            }, 0, 1000);
        }
    }

    public void stopSimulation() {
        timer.cancel();
        pane = new JOptionPane();
        JTextArea stats = new JTextArea(
                "Время симуляции: " + time + " c" +
                        "\nВсего объектов: " + Habitat.vehicles.size() +
                        "\nЧисло машин: " + h.carCount +
                        "\nЧисло мотоциклов: " + h.motoCount
        );
        stats.setFont(new Font("JetBrains Mono", Font.BOLD, 16));

        int n;
        if (showInfo.isSelected()) {
            n = JOptionPane.showConfirmDialog(this, stats, "Информация", JOptionPane.OK_CANCEL_OPTION);
        } else {
            n = JOptionPane.showConfirmDialog(this, "Завершить симуляцию?", "Завершение...", JOptionPane.OK_CANCEL_OPTION);
        }

        if (n == JOptionPane.YES_OPTION) {
            showTimer.setSelected(false);
            hideTimer.setSelected(false);
            if (isStarted) {
                stop.setEnabled(true);
                timer.cancel();
                isStarted = false;
            }

            if (willShowTime) {
                timeLabel.setVisible(false);
                willShowTime = false;
            }

            Habitat.vehicles.clear();
            start.setEnabled(true);
            stop.setEnabled(false);
            start.setContentAreaFilled(true);
            stop.setContentAreaFilled(false);

        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    h.update(time);
                    add(new JComponent(){});
                    time++;
                    timeLabel.setText("Время: " + time + " с");
                    repaint();
                }
            }, 0, 1000);
        }

    }

    public void getTimer() {
        if (!willShowTime) {
            timeLabel.setVisible(true);
            willShowTime = true;
            showTimer.setSelected(true);
            hideTimer.setSelected(false);
        } else {
            timeLabel.setVisible(false);
            willShowTime = false;
            hideTimer.setSelected(true);
            showTimer.setSelected(false);
        }
    }

    public MyFrame() {
        super("Road");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("JavaLabs/src/nstu/imgs/icon.png").getImage());
        setBounds(dimension.width / 2 - h.WIDTH / 2, dimension.height / 2 - h.HEIGHT / 2, h.WIDTH, h.HEIGHT);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel scene = new JPanel();
        scene.setLayout(new BorderLayout());
        add(scene);

        MyPanel road = new MyPanel();
        road.setBounds(0, 0, h.WIDTH - 350, h.HEIGHT);
        road.setLayout(new FlowLayout(FlowLayout.LEFT));
        road.setBackground(new Color(113, 200, 0));
        scene.add(road);

        timeLabel = new JLabel("Время: 0 с");
        timeLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        timeLabel.setForeground(Color.RED);
        timeLabel.setVisible(false);
        road.add(timeLabel);


        panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        panel.setPreferredSize(new Dimension(290, h.HEIGHT));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(22, 169, 173));
        scene.add(panel, BorderLayout.EAST);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'b' -> startSimulation();
                    case 'e' -> stopSimulation();
                    case 't' -> getTimer();
                    default -> {
                    }
                }
            }
        });

        start = new JButton("Старт");
        start.setBorderPainted(false);
        start.setBackground(Color.green);
        start.setPreferredSize(new Dimension(100, 30));
        start.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        start.addActionListener(e -> startSimulation());
        start.setFocusable(false);
        start.setContentAreaFilled(true);
        panel.add(start);

        stop = new JButton("Стоп");
        stop.setBorderPainted(false);
        stop.setBackground(Color.red);
        stop.addActionListener(e -> stopSimulation());
        stop.setEnabled(false);
        stop.setPreferredSize(new Dimension(100, 30));
        stop.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        stop.setFocusable(false);
        panel.add(stop);
        stop.setContentAreaFilled(false);

        showInfo = new JCheckBox("Показывать информацию");
        showInfo.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        showInfo.setBackground(panel.getBackground());
        showInfo.setFocusable(false);
        panel.add(showInfo);

        showTimer = new JRadioButton("Показывать время симуляции");
        showTimer.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        showTimer.setBackground(panel.getBackground());
        showTimer.addActionListener(e -> getTimer());
        showTimer.setFocusable(false);
        panel.add(showTimer);

        hideTimer = new JRadioButton("Скрывать время симуляции");
        hideTimer.setFont(new Font("JetBrains Mono", Font.BOLD, 14));
        hideTimer.setBackground(panel.getBackground());
        hideTimer.addActionListener(e -> getTimer());
        hideTimer.setFocusable(false);
        hideTimer.setSelected(true);
        panel.add(hideTimer);

        submitCar = new JButton("Ок");
        submitCar.setPreferredSize(new Dimension(50, 15));
        JLabel carsFreq = new JLabel("Частота появления машин");
        carsFreqText = new JTextField("" + h.N1, 2);
        carsFreq.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        carsFreqText.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        submitCar.addActionListener(e -> {
            try {
                h.N1 = Integer.parseInt(carsFreqText.getText());
                if (h.N1 <= 0) throw new Exception();
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, "Введите целое число!");
                h.N1 = 3;
                System.out.println("Поймано исключение " + exp.getMessage());
                carsFreqText.setText("" + h.N1);
            }
        });
        submitCar.setFocusable(false);
        panel.add(submitCar);
        panel.add(carsFreq);
        panel.add(carsFreqText);

        submitMoto = new JButton("Ок");
        submitMoto.setPreferredSize(new Dimension(50, 15));
        JLabel motoFreq = new JLabel("Частота появления мотоциклов");
        motoFreqText = new JTextField("" + h.N2, 2);
        motoFreq.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        motoFreqText.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        submitMoto.addActionListener(e -> {
            try {
                h.N2 = Integer.parseInt(motoFreqText.getText());
                if (h.N2 <= 0) throw new Exception();
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, "Введите целое число!");
                h.N2 = 4;
                System.out.println("Поймано исключение " + exp.getMessage());
                motoFreqText.setText("" + h.N2);
            }
        });
        submitMoto.setFocusable(false);
        panel.add(submitMoto);
        panel.add(motoFreq);
        panel.add(motoFreqText);

        String[] prob = {
                "0%",
                "10%",
                "20%",
                "30%",
                "40%",
                "50%",
                "60%",
                "70%",
                "80%",
                "90%",
                "100%",
        };

        JLabel carsP = new JLabel("Вероятность появления машин");
        carsP.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        panel.add(carsP);
        carProbability = new JComboBox<>(prob);
        carProbability.setSelectedItem("" + h.P1 + "%");
        panel.add(carProbability);

        carProbability.addActionListener(e -> {
            if (e.getSource() == carProbability) {
                String prb = Objects.requireNonNull(carProbability.getSelectedItem()).toString().replace("%", "");
                h.P1 = Integer.parseInt(prb);
            }
        });
        carProbability.setFocusable(false);

        JLabel motoP = new JLabel("Вероятность появления мотоциклов");
        motoP.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        panel.add(motoP);
        motoProbability = new JList<>(prob);
        motoProbability.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        motoProbability.setBackground(panel.getBackground());
        motoProbability.setVisibleRowCount(2);
        motoProbability.setSelectedIndex(7);
        motoProbability.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        motoProbability.addListSelectionListener(e -> {
            if (e.getSource() == motoProbability) {
                String prb = motoProbability.getSelectedValue().replace("%", "");
                h.P2 = Integer.parseInt(prb);
            }
        });
        motoProbability.setFocusable(false);
        panel.add(motoProbability);


        setVisible(true);
    }
}
