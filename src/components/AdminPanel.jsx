import React, { useState, useEffect } from 'react';
import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080/api' });

function AdminPanel() {
    const [disputes, setDisputes] = useState([]);
    const [note, setNote] = useState({});

    useEffect(() => {
        fetchDisputes();
    }, []);

    const fetchDisputes = async () => {
        try {
            const res = await api.get('/disputes');
            setDisputes(res.data.filter(d => d.status === 'PENDING'));
        } catch (err) {
            console.error(err);
        }
    };

    const handleResolve = async (disputeId, refundToSender) => {
        try {
            await api.post('/disputes/resolve', {
                disputeId,
                adminNote: note[disputeId] || 'Resolved by admin',
                refundToSender
            });
            fetchDisputes();
        } catch (err) {
            alert("Failed to resolve dispute");
        }
    };

    return (
        <div className="animate-fade-in glass-panel card">
            <h1 className="page-title" style={{ textAlign: 'left', marginBottom: '20px' }}>Admin Panel - Disputes</h1>
            {disputes.length === 0 ? <p style={{ color: 'var(--text-muted)' }}>No pending disputes.</p> : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    {disputes.map(d => (
                        <div key={d.id} style={{ background: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.2)', padding: '20px', borderRadius: '12px' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
                                <div>
                                    <h3 style={{ margin: '0 0 5px 0', color: 'var(--danger)' }}>Disputed Transaction ID: {d.transactionId}</h3>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Initiated by {d.initiatorId} at {new Date(d.createdAt).toLocaleString()}</div>
                                </div>
                                <span className="badge badge-disputed">PENDING</span>
                            </div>

                            <textarea
                                placeholder="Admin Resolution Note..."
                                value={note[d.id] || ''}
                                onChange={(e) => setNote({ ...note, [d.id]: e.target.value })}
                                rows="2"
                                style={{ marginBottom: '15px' }}
                            />

                            <div style={{ display: 'flex', gap: '15px' }}>
                                <button className="btn btn-secondary" style={{ borderColor: 'var(--warning)', color: 'var(--warning)' }} onClick={() => handleResolve(d.id, true)}>
                                    Reverse to Sender
                                </button>
                                <button className="btn btn-success" onClick={() => handleResolve(d.id, false)}>
                                    Release to Receiver
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default AdminPanel;
