export default function Settings() {
  return (
    <div>
      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ SCHEDULER ═══╗</div>
        <div style={{ padding: '10px' }}>
          <label>
            Enabled: <input type="checkbox" defaultChecked /> ON
          </label>
          <div style={{ marginTop: '10px' }}>
            Cron Schedule: <input type="text" defaultValue="0 0 9 * * ?" style={{ 
              background: 'transparent', 
              border: '1px solid var(--text-primary)', 
              color: 'var(--text-primary)',
              padding: '5px',
              fontFamily: 'Fira Code'
            }} />
          </div>
        </div>
      </div>

      <div className="terminal-box">
        <div className="terminal-box-title">╔═══ VIDEO CONFIGURATION ═══╗</div>
        <div style={{ padding: '10px' }}>
          <div>Duration: <input type="number" defaultValue="60" style={{ 
            background: 'transparent', 
            border: '1px solid var(--text-primary)', 
            color: 'var(--text-primary)',
            padding: '5px',
            width: '100px',
            fontFamily: 'Fira Code'
          }} /> seconds</div>
          <div style={{ marginTop: '10px' }}>
            Resolution: <input type="text" defaultValue="1080x1920" style={{ 
              background: 'transparent', 
              border: '1px solid var(--text-primary)', 
              color: 'var(--text-primary)',
              padding: '5px',
              fontFamily: 'Fira Code'
            }} />
          </div>
        </div>
      </div>

      <button className="button-primary">[SAVE CHANGES]</button>
    </div>
  );
}
