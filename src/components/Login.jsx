import React, { useState, useEffect } from 'react';
import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080/api' });

function Login({ onLogin }) {
    const [users, setUsers] = useState([]);
    const [name, setName] = useState('');
    const [startBalance, setStartBalance] = useState(1000);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const res = await api.get('/users');
            setUsers(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const createUser = async (e) => {
        e.preventDefault();
        try {
            await api.post('/users', { name, balance: parseFloat(startBalance), email: `${name.toLowerCase()}@example.com` });
            setName('');
            fetchUsers();
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="animate-fade-in" style={{ maxWidth: '500px', margin: '100px auto' }}>
            <h1 className="page-title">TrustPay</h1>
            <div className="glass-panel card">
                <h2 style={{ marginTop: 0 }}>Login as Existing User</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    {users.map(u => (
                        <button key={u.id} className="btn btn-secondary" onClick={() => onLogin(u)}>
                            Login as {u.name} (${u.balance})
                        </button>
                    ))}
                    <button className="btn btn-danger" onClick={() => onLogin({ id: 'admin', name: 'Admin' })}>
                        Login as Admin
                    </button>
                </div>
            </div>

            <div className="glass-panel card">
                <h2>Create New User</h2>
                <form onSubmit={createUser}>
                    <input type="text" placeholder="Name" value={name} onChange={e => setName(e.target.value)} required />
                    <input type="number" placeholder="Starting Balance" value={startBalance} onChange={e => setStartBalance(e.target.value)} required />
                    <button type="submit" className="btn" style={{ width: '100%' }}>Create Account</button>
                </form>
            </div>
        </div>
    );
}

export default Login;
