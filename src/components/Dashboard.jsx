import React, { useState, useEffect } from 'react';
import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080/api' });

function Dashboard({ user }) {
    const [balance, setBalance] = useState(user.balance);
    const [users, setUsers] = useState([]);
    const [transactions, setTransactions] = useState([]);

    // Form state
    const [recipientId, setRecipientId] = useState('');
    const [amount, setAmount] = useState('');
    const [statement, setStatement] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const uRes = await api.get(`/users/${user.id}`);
            setBalance(uRes.data.balance);

            const pRes = await api.get('/users');
            setUsers(pRes.data.filter(u => u.id !== user.id));

            const tRes = await api.get('/transactions');
            setTransactions(tRes.data.filter(t => t.senderId === user.id || t.receiverId === user.id).reverse());

        } catch (err) {
            console.error(err);
        }
    };

    const handleSend = async (e) => {
        e.preventDefault();
        if (!recipientId || !amount) return;
        try {
            await api.post('/transactions/initiate', {
                senderId: user.id,
                receiverId: recipientId,
                amount: parseFloat(amount),
                statementText: statement
            });
            setRecipientId('');
            setAmount('');
            setStatement('');
            fetchData();
        } catch (err) {
            alert("Failed to send: " + (err.response?.data || err.message));
        }
    };

    const handleConfirm = async (txId) => {
        try {
            await api.post('/transactions/confirm', { transactionId: txId, userId: user.id });
            fetchData();
        } catch (err) {
            alert("Failed to confirm");
        }
    };

    const handleDispute = async (txId) => {
        try {
            await api.post('/disputes/trigger', { transactionId: txId, initiatorId: user.id });
            fetchData();
        } catch (err) {
            alert("Failed to dispute");
        }
    };

    const pendingConfirmations = transactions.filter(t => t.receiverId === user.id && t.status === 'PENDING');
    const history = transactions;

    return (
        <div className="animate-fade-in">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <h1 className="page-title" style={{ margin: 0, fontSize: '2rem' }}>Dashboard</h1>
                <div className="glass-panel" style={{ padding: '15px 30px', display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1px' }}>Available Balance</span>
                    <span style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--success)' }}>${balance.toFixed(2)}</span>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'minmax(300px, 1fr) 2fr', gap: '30px' }}>

                {/* Left Column */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>

                    <div className="glass-panel card">
                        <h2>Send Money</h2>
                        <form onSubmit={handleSend}>
                            <select value={recipientId} onChange={e => setRecipientId(e.target.value)} required>
                                <option value="" disabled>Select Recipient</option>
                                {users.map(u => <option key={u.id} value={u.id}>{u.name}</option>)}
                            </select>

                            <input type="number" placeholder="Amount ($)" value={amount} onChange={e => setAmount(e.target.value)} required min="1" step="0.01" />

                            <textarea placeholder="Statement (e.g., Bought Item X)" value={statement} onChange={e => setStatement(e.target.value)} rows="3" required></textarea>

                            <button type="submit" className="btn btn-success" style={{ width: '100%' }}>Initiate Transfer</button>
                        </form>
                    </div>

                </div>

                {/* Right Column */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>

                    {pendingConfirmations.length > 0 && (
                        <div className="glass-panel card" style={{ borderLeft: '4px solid var(--warning)' }}>
                            <h2 style={{ marginTop: 0, color: 'var(--warning)' }}>Action Required: Pending Confirmations</h2>
                            {pendingConfirmations.map(tx => {
                                const sender = users.find(u => u.id === tx.senderId)?.name || 'Unknown';
                                return (
                                    <div key={tx.id} style={{ padding: '15px', background: 'rgba(255,255,255,0.03)', borderRadius: '8px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <div>
                                            <div style={{ fontWeight: 600, fontSize: '1.2rem', marginBottom: '4px' }}>${tx.amount.toFixed(2)} from {sender}</div>
                                            <div style={{ color: 'var(--text-muted)' }}>Status: Escrow/Held</div>
                                        </div>
                                        <div style={{ display: 'flex', gap: '10px' }}>
                                            <button className="btn btn-success" onClick={() => handleConfirm(tx.id)}>Confirm Statement</button>
                                            <button className="btn btn-danger" onClick={() => handleDispute(tx.id)}>Dispute</button>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}

                    <div className="glass-panel card">
                        <h2>Transaction History</h2>
                        {history.length === 0 ? <p style={{ color: 'var(--text-muted)' }}>No transactions yet.</p> : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                {history.map(tx => {
                                    const isSender = tx.senderId === user.id;
                                    const otherPartyId = isSender ? tx.receiverId : tx.senderId;
                                    const otherParty = users.find(u => u.id === otherPartyId)?.name || 'Unknown';

                                    return (
                                        <div key={tx.id} style={{ padding: '16px', background: 'rgba(255,255,255,0.02)', borderRadius: '12px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid rgba(255,255,255,0.05)' }}>
                                            <div>
                                                <div style={{ fontWeight: 600, fontSize: '1.1rem', marginBottom: '4px' }}>
                                                    {isSender ? `Sent to ${otherParty}` : `Received from ${otherParty}`}
                                                    {tx.fastTransaction && <span style={{ marginLeft: '10px', fontSize: '0.8rem', background: 'rgba(79, 70, 229, 0.2)', color: 'var(--primary)', padding: '2px 6px', borderRadius: '4px' }}>FAST</span>}
                                                </div>
                                                <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                                                    Amount: ${tx.amount.toFixed(2)}
                                                </div>
                                            </div>
                                            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '8px' }}>
                                                <span className={`badge badge-${tx.status.toLowerCase()}`}>{tx.status}</span>
                                                {tx.status === 'PENDING' && isSender && (
                                                    <button className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '0.8rem' }} onClick={() => handleDispute(tx.id)}>Dispute</button>
                                                )}
                                            </div>
                                        </div>
                                    )
                                })}
                            </div>
                        )}
                    </div>

                </div>
            </div>
        </div>
    );
}

export default Dashboard;
