import React, { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import { Bell, CheckCircle } from "lucide-react";

dayjs.extend(relativeTime);

const API_URL = "http://localhost:8060";
const SOCKET_URL = `${API_URL}/ws`;
const NOTIFICATIONS_API = `${API_URL}/api/notification/all`;

const StudentNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [connectionStatus, setConnectionStatus] = useState("Disconnected");

  useEffect(() => {
    console.log("Fetching existing notifications...");
    fetch(NOTIFICATIONS_API)
      .then((res) => res.json())
      .then((data) => {
        console.log("Fetched notifications:", data);
        setNotifications(data);
      })
      .catch((error) => console.error("Error fetching notifications:", error));

    console.log("Setting up WebSocket connection to:", SOCKET_URL);
    let stompClient = null;

    stompClient = new Client({
      webSocketFactory: () => new SockJS(SOCKET_URL),
      debug: (str) => console.log("STOMP Debug:", str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        console.log("Connected to WebSocket:", frame);
        setConnectionStatus("Connected");

        stompClient.subscribe("/topic/notifications", (message) => {
          console.log("Received raw message:", message.body);
          try {
            const newNotification = JSON.parse(message.body);
            console.log("Parsed notification:", newNotification);
            setNotifications((prev) => [newNotification, ...prev]);
          } catch (error) {
            console.error("Error parsing notification:", error);
          }
        });
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame.headers.message);
        setConnectionStatus("Error: " + frame.headers.message);
      },
      onWebSocketError: (event) => {
        console.error("WebSocket error:", event);
        setConnectionStatus("Connection Error");
      },
      onWebSocketClose: (event) => {
        console.error("WebSocket closed:", event);
        setConnectionStatus("Disconnected");
      }
    });

    console.log("Activating STOMP client");
    stompClient.activate();

    return () => {
      console.log("Cleaning up WebSocket connection");
      if (stompClient && stompClient.active) {
        stompClient.deactivate();
      }
    };
  }, []);

  return (
    <div className="max-w-3xl mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-semibold flex items-center gap-2">
          <Bell className="w-6 h-6 text-blue-500" />
          Notifications
        </h2>
        <span className={`text-sm ${connectionStatus === "Connected" ? "text-green-500" : "text-red-500"}`}>
          {connectionStatus}
        </span>
      </div>

      {notifications.length === 0 ? (
        <p className="text-gray-500 mt-4">No notifications yet</p>
      ) : (
        <div className="mt-4 space-y-3">
          {notifications.map((notification, index) => (
            <div
              key={notification.id || index}
              className="p-4 border rounded-lg flex items-start gap-3 bg-gray-100 shadow-sm"
            >
              <CheckCircle className="w-6 h-6 text-green-500 flex-shrink-0" />
              <div>
                <h3 className="font-medium">{notification.title}</h3>
                <p className="text-gray-600">{notification.message}</p>
                <span className="text-sm text-gray-400">
                  {dayjs(notification.timestamp).fromNow()}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default StudentNotifications;
