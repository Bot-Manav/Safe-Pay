import React, { useEffect, useState } from 'react';
import axios from 'axios';

export default function LedgerList() {
  const [ledgers, setLedgers] = useState([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/ledgers');
        setLedgers(res.data || []);
      } catch (err) {
        console.error('Failed to fetch ledgers', err);
      }
    };
    fetch();
  }, []);

  return (
    <div>
      <h2>Ledger Entries</h2>
      <ul>
        {ledgers.map((l) => (
          <li key={l.id}>
            Tx: {l.transactionId} — Account: {l.accountId} — {l.entryType} — Amount: {l.amount}
          </li>
        ))}
      </ul>
    </div>
  );
}
