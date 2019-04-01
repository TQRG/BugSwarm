/*
 * Hello Minecraft!.
 * Copyright (C) 2013  huangyuhui <huanghongxun2008@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hellominecraft.util.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.SwingUtilities;
import org.jackhuang.hellominecraft.util.C;
import org.jackhuang.hellominecraft.util.logging.HMCLog;
import org.jackhuang.hellominecraft.util.MessageBox;
import org.jackhuang.hellominecraft.util.StrUtils;
import org.jackhuang.hellominecraft.util.ui.SwingUtils;

/**
 *
 * @author huangyuhui
 */
public class TaskWindow extends javax.swing.JDialog
    implements ProgressProviderListener, Runnable, DoingDoneListener<Task> {

    private static volatile TaskWindow INSTANCE = null;

    private static synchronized TaskWindow instance() {
        if (INSTANCE == null)
            INSTANCE = new TaskWindow();
        INSTANCE.clean();
        return INSTANCE;
    }

    public static TaskWindowFactory factory() {
        return new TaskWindowFactory();
    }

    boolean suc = false;

    private TaskList taskList;
    private final ArrayList<String> failReasons = new ArrayList();
    private String stackTrace = null, lastStackTrace = null;

    /**
     * Creates new form DownloadWindow
     */
    private TaskWindow() {
        initComponents();

        setLocationRelativeTo(null);

        if (lstDownload.getColumnModel().getColumnCount() > 1) {
            int i = 35;
            lstDownload.getColumnModel().getColumn(1).setMinWidth(i);
            lstDownload.getColumnModel().getColumn(1).setMaxWidth(i);
            lstDownload.getColumnModel().getColumn(1).setPreferredWidth(i);
        }

        setModal(true);
    }

    public TaskWindow addTask(Task task) {
        taskList.addTask(task);
        return this;
    }

    public synchronized void clean() {
        if (isVisible())
            return;
        taskList = new TaskList();
        taskList.addTaskListener(this);
        taskList.addAllDoneListener(this);
    }

    public static String downloadSource = "";

    public boolean start() {
        if (isVisible() || taskList == null || taskList.isAlive())
            return false;
        pgsTotal.setValue(0);
        suc = false;
        SwingUtils.clearDefaultTable(lstDownload);
        failReasons.clear();
        tasks.clear();
        try {
            taskList.start();
        } catch (Exception e) {
            HMCLog.err("Failed to start thread, maybe there're already a taskwindow here.", e);
            HMCLog.err("There's the stacktrace of the this invoking.");
            HMCLog.err(stackTrace);
            HMCLog.err("There's the stacktrace of the last invoking.");
            HMCLog.err(lastStackTrace);
            MessageBox.Show(C.i18n("taskwindow.no_more_instance"));
            return false;
        }
        setTitle(C.i18n("taskwindow.title") + " - " + C.i18n("download.source") + ": " + downloadSource);
        this.setVisible(true);
        return this.areTasksFinished();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCancel = new javax.swing.JButton();
        pgsTotal = new javax.swing.JProgressBar();
        srlDownload = new javax.swing.JScrollPane();
        lstDownload = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(C.i18n("taskwindow.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        btnCancel.setText(C.i18n("taskwindow.cancel")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        pgsTotal.setStringPainted(true);

        lstDownload.setModel(SwingUtils.makeDefaultTableModel(new String[]{C.i18n("taskwindow.file_name"), C.i18n("taskwindow.download_progress")}, new Class[]{String.class, String.class}, new boolean[]{false,false})
        );
        lstDownload.setRowSelectionAllowed(false);
        lstDownload.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        srlDownload.setViewportView(lstDownload);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pgsTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
            .addComponent(srlDownload, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(srlDownload, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pgsTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (MessageBox.Show(C.i18n("operation.confirm_stop"), MessageBox.YES_NO_OPTION) == MessageBox.YES_OPTION)
            this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if (taskList == null)
            return;
        tasks.clear();

        if (!this.failReasons.isEmpty()) {
            String str = StrUtils.parseParams("", failReasons.toArray(), "\n");
            SwingUtilities.invokeLater(() -> MessageBox.Show(str, C.i18n("message.error"), MessageBox.ERROR_MESSAGE));
            failReasons.clear();
        }

        if (!suc) {
            if (taskList != null)
                SwingUtilities.invokeLater(taskList::abort);
            HMCLog.log("Tasks have been canceled by user.");
        }
        taskList = null;
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JTable lstDownload;
    private javax.swing.JProgressBar pgsTotal;
    private javax.swing.JScrollPane srlDownload;
    // End of variables declaration//GEN-END:variables

    final ArrayList<Task> tasks = new ArrayList<>();
    final ArrayList<Integer> progresses = new ArrayList<>();

    @Override
    public void setProgress(Task task, int progress, int max) {
        SwingUtilities.invokeLater(() -> {
            if (task == null)
                return;
            int idx = tasks.indexOf(task);
            if (idx == -1)
                return;
            int pgs = progress * 100 / max;
            if (progresses.contains(idx) && progresses.get(idx) != pgs && lstDownload.getRowCount() > idx) {
                SwingUtils.setValueAt(lstDownload, pgs + "%", idx, 1);
                progresses.set(idx, pgs);
            }
        });
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            dispose();
            suc = true;
            HMCLog.log("Tasks are finished.");
        });
    }

    @Override
    public void onDoing(Task task, Collection<Task> taskCollection) {
        if (task == null)
            return;
        task.setProgressProviderListener(this);

        SwingUtilities.invokeLater(() -> {
            if (taskList == null)
                return;
            tasks.add(task);
            progresses.add(0);
            SwingUtils.appendLast(lstDownload, task.getInfo(), "0%");
            SwingUtils.moveEnd(srlDownload);
        });
    }

    public boolean areTasksFinished() {
        return suc;
    }

    @Override
    public void onDone(Task task, Collection<Task> taskCollection) {
        SwingUtilities.invokeLater(() -> {
            if (taskList == null || task == null)
                return;
            pgsTotal.setMaximum(taskList.taskCount());
            pgsTotal.setValue(pgsTotal.getValue() + 1);
            int idx = tasks.indexOf(task);
            if (idx == -1)
                return;
            tasks.remove(idx);
            progresses.remove(idx);
            SwingUtils.removeRow(lstDownload, idx);
        });
    }

    @Override
    public void onFailed(Task task) {
        SwingUtilities.invokeLater(() -> {
            if (taskList == null || task == null)
                return;
            String msg = null;
            if (task.getFailReason() != null && !(task.getFailReason() instanceof NoShownTaskException))
                if (StrUtils.isBlank(task.getFailReason().getLocalizedMessage()))
                    msg = task.getFailReason().getClass().getSimpleName();
                else
                    msg = task.getFailReason().getLocalizedMessage();
            if (msg != null)
                failReasons.add(task.getInfo() + ": " + msg);
            pgsTotal.setMaximum(taskList.taskCount());
            pgsTotal.setValue(pgsTotal.getValue() + 1);
            int idx = tasks.indexOf(task);
            if (idx == -1)
                return;
            SwingUtils.setValueAt(lstDownload, task.getFailReason(), idx, 0);
            SwingUtils.setValueAt(lstDownload, "0%", idx, 1);
            SwingUtils.moveEnd(srlDownload);
        });
    }

    @Override
    public void onProgressProviderDone(Task task) {

    }

    @Override
    public void setStatus(Task task, String sta) {
        SwingUtilities.invokeLater(() -> {
            if (taskList == null || task == null)
                return;
            int idx = tasks.indexOf(task);
            if (idx == -1)
                return;
            SwingUtils.setValueAt(lstDownload, task.getInfo() + ": " + sta, idx, 0);
        });
    }

    public static boolean execute(Task... ts) {
        TaskWindowFactory f = factory();
        for (Task t : ts)
            f.append(t);
        return f.create();
    }

    public static class TaskWindowFactory {

        LinkedList<Task> ll = new LinkedList<>();
        boolean flag;

        public TaskWindowFactory append(Task t) {
            ll.add(t);
            return this;
        }

        public boolean create() {
            String stacktrace = StrUtils.getStackTrace(new Throwable());
            return SwingUtils.invokeAndWait(() -> {
                final TaskWindow tw = instance();
                synchronized (tw) {
                    if (tw.isVisible())
                        return false;
                    for (Task t : ll)
                        tw.addTask(t);
                    tw.lastStackTrace = tw.stackTrace;
                    tw.stackTrace = stacktrace;
                    return tw.start();
                }
            });
        }
    }
}
