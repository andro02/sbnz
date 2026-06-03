import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export function useCepNotifications() {
    const [notifications, setNotifications] = useState([]);
    const [toasts, setToasts] = useState([]);
    const clientRef = useRef(null);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS("/ws"),
            reconnectDelay: 5000,
            onConnect: () => {
                client.subscribe("/topic/cep-notifications", (message) => {
                    const notification = JSON.parse(message.body);
                    const id = Date.now() + Math.random();

                    setNotifications(prev => [{ ...notification, id, seenAt: Date.now() }, ...prev]);
                    setToasts(prev => [{ ...notification, id }, ...prev]);

                    setTimeout(() => {
                        setToasts(prev => prev.filter(t => t.id !== id));
                    }, 5000);
                });
            },
        });

        client.activate();
        clientRef.current = client;

        return () => client.deactivate();
    }, []);

    function dismissToast(id) {
        setToasts(prev => prev.filter(t => t.id !== id));
    }

    function clearNotifications() {
        setNotifications([]);
    }

    return { notifications, toasts, dismissToast, clearNotifications };
}