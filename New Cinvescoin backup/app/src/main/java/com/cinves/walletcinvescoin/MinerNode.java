package com.cinves.walletcinvescoin;

import amp_new.AppInterface.Node;
import amp_new.Blockchain.Block;
import amp_new.Blockchain.Blockchain;
import amp_new.Blockchain.Transaction;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class MinerNode extends com.cinves.walletcinvescoin.Node implements Serializable {

    public ArrayList<Transaction> transactionsSaved;
    public int numberOfTransactions;

    public MinerNode() {
        super();
        this.transactionsSaved = new ArrayList<>();
        this.numberOfTransactions = 0;
    }

    public abstract void registerTransaction(Transaction transaction, API api);

    public void clearTransactionsSaved() {
        this.transactionsSaved.clear();
    }

    public abstract void createBlock(API api);

    /**
     * Método que sobreescribirá la clase que herede de esta
     *
     * @return
     */
    public abstract Transaction rewardOfPow();

}
