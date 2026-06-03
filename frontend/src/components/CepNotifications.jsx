import { useState } from "react";
import { LuBell, LuX, LuTriangleAlert, LuDroplets, LuUsers, LuLayers } from "react-icons/lu";
import "../css/CepNotifications.css";

const TYPE_META = {
    CEP_RIVER:    { icon: <LuDroplets size={13} />,      color: "cep-water",      label: "Water Risk"   },
    CEP_CITY:     { icon: <LuUsers size={13} />,         color: "cep-city",       label: "Urban Risk"   },
    ENV_AGENCY:   { icon: <LuLayers size={13} />,        color: "cep-cluster",    label: "Cluster"      },
};

function getTypeMeta(type) {
    return TYPE_META[type] ?? { icon: <LuTriangleAlert size={13} />, color: "cep-default", label: "Alert" };
}

function formatTime(ts) {
    return new Date(ts).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

export function CepToasts({ toasts, onDismiss }) {
    return (
        <div className="cep-toasts">
            {toasts.map(t => {
                const meta = getTypeMeta(t.type);
                return (
                    <div key={t.id} className={`cep-toast ${meta.color}`}>
                        <span className="cep-toast-icon">{meta.icon}</span>
                        <div className="cep-toast-body">
                            <span className="cep-toast-label">{meta.label}</span>
                            <span className="cep-toast-msg">{t.message}</span>
                        </div>
                        <button className="cep-toast-close" onClick={() => onDismiss(t.id)}>
                            <LuX size={12} />
                        </button>
                    </div>
                );
            })}
        </div>
    );
}

export function CepBell({ notifications, onClear }) {
    const [open, setOpen] = useState(false);
    const unread = notifications.length;

    return (
        <>
            <button className={`cep-bell ${unread > 0 ? "has-notif" : ""}`} onClick={() => setOpen(o => !o)}>
                <LuBell size={16} />
                {unread > 0 && <span className="cep-bell-badge">{unread > 99 ? "99+" : unread}</span>}
            </button>

            {open && (
                <div className="cep-panel">
                    <div className="cep-panel-header">
                        <span>CEP Notifications</span>
                        <div className="cep-panel-actions">
                            {notifications.length > 0 && (
                                <button className="cep-panel-clear" onClick={onClear}>Clear all</button>
                            )}
                            <button className="cep-panel-close" onClick={() => setOpen(false)}>
                                <LuX size={13} />
                            </button>
                        </div>
                    </div>

                    <div className="cep-panel-list">
                        {notifications.length === 0 && (
                            <div className="cep-panel-empty">No notifications this session</div>
                        )}
                        {notifications.map(n => {
                            const meta = getTypeMeta(n.type);
                            return (
                                <div key={n.id} className={`cep-panel-item ${meta.color}`}>
                                    <span className="cep-panel-item-icon">{meta.icon}</span>
                                    <div className="cep-panel-item-body">
                                        <div className="cep-panel-item-top">
                                            <span className="cep-panel-item-label">{meta.label}</span>
                                            <span className="cep-panel-item-time">{formatTime(n.seenAt)}</span>
                                        </div>
                                        <span className="cep-panel-item-msg">{n.message}</span>
                                        <span className="cep-panel-item-target">{n.recipient} · #{n.dumpsiteId}</span>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </>
    );
}
