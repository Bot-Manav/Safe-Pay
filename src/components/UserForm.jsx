import React, { useState } from 'react';
import axios from 'axios';

export default function UserForm() {
  const [form, setForm] = useState({ name: '', email: '', balance: '' });
  const [loading, setLoading] = useState(false);

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = { ...form, balance: Number(form.balance || 0) };
      const res = await axios.post('http://localhost:8080/api/users', payload);
      alert('Created user: ' + (res.data?.name || '')); 
      setForm({ name: '', email: '', balance: '' });
    } catch (err) {
      console.error(err);
      alert('Error creating user');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Create User</h2>
      <form onSubmit={onSubmit}>
        <div>
          <label>Name</label>
          <input name="name" value={form.name} onChange={onChange} required />
        </div>
        <div>
          <label>Email</label>
          <input name="email" value={form.email} onChange={onChange} required />
        </div>
        <div>
          <label>Balance</label>
          <input name="balance" type="number" value={form.balance} onChange={onChange} />
        </div>
        <button type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create'}</button>
      </form>
    </div>
  );
}
