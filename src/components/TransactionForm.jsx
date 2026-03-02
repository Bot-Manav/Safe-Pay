import React, { useState } from 'react';
import axios from 'axios';

function TransactionForm() {
  const [formData, setFormData] = useState({ senderId: '', receiverId: '', amount: '' });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/api/transactions', formData);
      alert('Transaction created: ' + response.data.id);
    } catch (error) {
      console.error(error);
      alert('Error creating transaction');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="senderId" placeholder="Sender ID" onChange={handleChange} />
      <input name="receiverId" placeholder="Receiver ID" onChange={handleChange} />
      <input name="amount" placeholder="Amount" type="number" onChange={handleChange} />
      <button type="submit">Create Transaction</button>
    </form>
  );
}

export default TransactionForm;