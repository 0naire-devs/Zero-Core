package kr.hyfata.zero.modules.gui.mailbox;

import java.util.ArrayList;

public class MailboxInventoryInfo {
    private ArrayList<Mailbox> mailboxes;
    private int currentPage = 0;

    public ArrayList<Mailbox> getMailboxes() {
        return mailboxes;
    }

    public void setMailboxes(ArrayList<Mailbox> mailboxes) {
        this.mailboxes = mailboxes;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
