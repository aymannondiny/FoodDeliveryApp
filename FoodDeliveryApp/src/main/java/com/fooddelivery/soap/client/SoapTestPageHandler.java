package com.fooddelivery.soap.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Serves the SOAP test HTML page at /soap-test
 */
public class SoapTestPageHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String html = buildHtml();
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String buildHtml() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🍔 Food Delivery - SOAP Test Client</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            color: #2c3e50;
            padding: 20px;
        }
        h1 {
            color: #FF6B35;
            margin-bottom: 8px;
            font-size: 28px;
        }
        .subtitle {
            color: #7f8c8d;
            margin-bottom: 24px;
            font-size: 14px;
        }
        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-bottom: 24px;
        }
        .card {
            background: white;
            border: 1px solid #dedede;
            border-radius: 8px;
            padding: 16px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .card h3 {
            color: #2c3e50;
            margin-bottom: 12px;
            font-size: 16px;
            border-bottom: 2px solid #FF6B35;
            padding-bottom: 6px;
        }
        .form-group {
            margin-bottom: 10px;
        }
        .form-group label {
            display: block;
            font-size: 12px;
            color: #7f8c8d;
            margin-bottom: 4px;
        }
        .form-group input {
            width: 100%;
            padding: 8px 10px;
            border: 1px solid #dedede;
            border-radius: 4px;
            font-size: 13px;
        }
        button {
            background: #FF6B35;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 13px;
            font-weight: bold;
            margin-top: 6px;
            margin-right: 6px;
        }
        button:hover { background: #CC5000; }
        button.secondary {
            background: white;
            color: #2c3e50;
            border: 1px solid #dedede;
        }
        button.secondary:hover { background: #f0f0f0; }
        .result-area {
            margin-top: 24px;
        }
        .result-area h3 {
            color: #2c3e50;
            margin-bottom: 8px;
        }
        .tabs {
            display: flex;
            gap: 4px;
            margin-bottom: 8px;
        }
        .tab {
            padding: 8px 16px;
            background: #e0e0e0;
            border: none;
            border-radius: 6px 6px 0 0;
            cursor: pointer;
            font-size: 13px;
            font-weight: bold;
        }
        .tab.active {
            background: #2c3e50;
            color: white;
        }
        #requestBox, #responseBox {
            width: 100%;
            min-height: 300px;
            font-family: 'Consolas', 'Courier New', monospace;
            font-size: 12px;
            padding: 12px;
            border: 1px solid #dedede;
            border-radius: 0 8px 8px 8px;
            background: #1e1e1e;
            color: #d4d4d4;
            white-space: pre-wrap;
            overflow-x: auto;
            line-height: 1.5;
        }
        .status {
            margin-top: 8px;
            font-size: 13px;
            padding: 8px 12px;
            border-radius: 4px;
        }
        .status.success { background: #d4edda; color: #155724; }
        .status.error { background: #f8d7da; color: #721c24; }
        .status.loading { background: #fff3cd; color: #856404; }
        .hidden { display: none; }
        .badge {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 11px;
            font-weight: bold;
            margin-left: 6px;
        }
        .badge.soap { background: #3498db; color: white; }
        .badge.rest { background: #27ae60; color: white; }
        .comparison {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-top: 16px;
        }
        .comparison pre {
            min-height: 200px;
            font-family: 'Consolas', monospace;
            font-size: 11px;
            padding: 10px;
            border: 1px solid #dedede;
            border-radius: 6px;
            background: #1e1e1e;
            color: #d4d4d4;
            white-space: pre-wrap;
            overflow-x: auto;
        }
        .comparison h4 {
            margin-bottom: 6px;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <h1>🍔 Food Delivery - SOAP Test Client</h1>
    <p class="subtitle">
        Manual SOAP request builder &amp; response viewer
        <span class="badge soap">SOAP :9090</span>
        <span class="badge rest">REST :8080</span>
    </p>

    <div class="grid">
        <!-- Restaurant Operations -->
        <div class="card">
            <h3>🏪 Restaurant Operations</h3>
            <button onclick="callSoap('restaurants', 'getAllRestaurants', {})">
                Get All Restaurants
            </button>
            <button onclick="callSoap('restaurants', 'getAllCuisineTypes', {})">
                Get Cuisine Types
            </button>
            <div class="form-group" style="margin-top:10px">
                <label>Search Query:</label>
                <input type="text" id="searchQuery" placeholder="e.g. Burger" value="Burger">
            </div>
            <button onclick="callSoap('restaurants', 'searchRestaurants', {arg0: document.getElementById('searchQuery').value})">
                Search Restaurants
            </button>
            <div class="form-group">
                <label>Cuisine Type:</label>
                <input type="text" id="cuisineType" placeholder="e.g. Chinese" value="Chinese">
            </div>
            <button onclick="callSoap('restaurants', 'filterByCuisine', {arg0: document.getElementById('cuisineType').value})">
                Filter by Cuisine
            </button>
        </div>

        <!-- Menu Operations -->
        <div class="card">
            <h3>🍴 Menu Operations</h3>
            <div class="form-group">
                <label>Restaurant ID:</label>
                <input type="text" id="menuRestaurantId" placeholder="e.g. RST-XXXXXXXX">
            </div>
            <button onclick="callSoap('menu', 'getMenu', {arg0: document.getElementById('menuRestaurantId').value})">
                Get Menu
            </button>
            <p style="margin-top:8px; font-size:12px; color:#7f8c8d">
                💡 Get restaurant IDs from "Get All Restaurants" first
            </p>
        </div>

        <!-- Order Operations -->
        <div class="card">
            <h3>📦 Order Operations</h3>
            <div class="form-group">
                <label>Order ID:</label>
                <input type="text" id="orderId" placeholder="e.g. ORD-XXXXXXXX">
            </div>
            <button onclick="callSoap('orders', 'getOrder', {arg0: document.getElementById('orderId').value})">
                Get Order Details
            </button>
            <button onclick="callSoap('orders', 'trackOrder', {arg0: document.getElementById('orderId').value})">
                Track Order
            </button>
        </div>

        <!-- Coupon Operations -->
        <div class="card">
            <h3>🎟️ Coupon Operations</h3>
            <div class="form-group">
                <label>Coupon Code:</label>
                <input type="text" id="couponCode" placeholder="e.g. WELCOME20" value="WELCOME20">
            </div>
            <div class="form-group">
                <label>Subtotal (BDT):</label>
                <input type="number" id="couponSubtotal" placeholder="e.g. 500" value="500">
            </div>
            <button onclick="callSoap('coupons', 'validateCoupon', {arg0: document.getElementById('couponCode').value, arg1: document.getElementById('couponSubtotal').value})">
                Validate Coupon
            </button>
        </div>
    </div>

    <!-- Compare SOAP vs REST -->
    <div class="card" style="margin-bottom: 16px">
        <h3>⚡ Compare SOAP vs REST</h3>
        <button onclick="compareFormats()">
            Compare: Get All Restaurants (SOAP vs REST)
        </button>
        <div id="comparisonArea" class="comparison hidden">
            <div>
                <h4><span class="badge soap">SOAP</span> XML Response</h4>
                <pre id="soapCompare"></pre>
            </div>
            <div>
                <h4><span class="badge rest">REST</span> JSON Response</h4>
                <pre id="restCompare"></pre>
            </div>
        </div>
    </div>

    <!-- Results -->
    <div class="result-area">
        <h3>📋 Request / Response</h3>
        <div class="tabs">
            <button class="tab active" onclick="showTab('request')">📤 SOAP Request</button>
            <button class="tab" onclick="showTab('response')">📥 SOAP Response</button>
        </div>
        <div id="statusBar" class="status hidden"></div>
        <pre id="requestBox">Click a button above to send a SOAP request...</pre>
        <pre id="responseBox" class="hidden">Response will appear here...</pre>
    </div>

    <script>
        function showTab(tab) {
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            if (tab === 'request') {
                document.getElementById('requestBox').classList.remove('hidden');
                document.getElementById('responseBox').classList.add('hidden');
                document.querySelectorAll('.tab')[0].classList.add('active');
            } else {
                document.getElementById('requestBox').classList.add('hidden');
                document.getElementById('responseBox').classList.remove('hidden');
                document.querySelectorAll('.tab')[1].classList.add('active');
            }
        }

        function setStatus(message, type) {
            const bar = document.getElementById('statusBar');
            bar.textContent = message;
            bar.className = 'status ' + type;
            bar.classList.remove('hidden');
        }

        function buildSoapEnvelope(namespace, method, params) {
            let body = '';
            for (const [key, value] of Object.entries(params)) {
                if (value !== undefined && value !== '') {
                    body += '      <' + key + '>' + escapeXml(String(value)) + '</' + key + '>\\n';
                }
            }

            return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:ns="${namespace}">
  <soapenv:Header/>
  <soapenv:Body>
    <ns:${method}>
${body}    </ns:${method}>
  </soapenv:Body>
</soapenv:Envelope>`;
        }

        function escapeXml(str) {
            return str.replace(/&/g, '&amp;')
                      .replace(/</g, '&lt;')
                      .replace(/>/g, '&gt;')
                      .replace(/"/g, '&quot;');
        }

        function formatXml(xml) {
            let formatted = '';
            let indent = 0;
            const lines = xml.replace(/>\\s*</g, '>\\n<').split('\\n');

            lines.forEach(line => {
                line = line.trim();
                if (!line) return;

                if (line.startsWith('</')) {
                    indent = Math.max(0, indent - 1);
                }

                formatted += '  '.repeat(indent) + line + '\\n';

                if (line.startsWith('<') && !line.startsWith('</') &&
                    !line.startsWith('<?') && !line.endsWith('/>') &&
                    !line.includes('</')) {
                    indent++;
                }
            });

            return formatted.trim();
        }

        async function callSoap(service, method, params) {
            const namespace = 'http://fooddelivery.com/soap';
            const envelope = buildSoapEnvelope(namespace, method, params);

            document.getElementById('requestBox').textContent = envelope;
            showTab('request');
            setStatus('⏳ Sending SOAP request...', 'loading');

            try {
                const proxyUrl = '/soap-proxy?service=' + encodeURIComponent(service) +
                                 '&method=' + encodeURIComponent(method) +
                                 '&body=' + encodeURIComponent(envelope);

                const response = await fetch(proxyUrl);
                const text = await response.text();

                document.getElementById('responseBox').textContent = formatXml(text);

                if (response.ok) {
                    setStatus('✅ Response received (HTTP ' + response.status + ')', 'success');
                } else {
                    setStatus('❌ Error (HTTP ' + response.status + ')', 'error');
                }

                showTab('response');

            } catch (error) {
                document.getElementById('responseBox').textContent = 'Error: ' + error.message;
                setStatus('❌ Connection failed: ' + error.message, 'error');
                showTab('response');
            }
        }

        async function compareFormats() {
            setStatus('⏳ Fetching both SOAP and REST responses...', 'loading');

            try {
                // SOAP
                const namespace = 'http://fooddelivery.com/soap';
                const envelope = buildSoapEnvelope(namespace, 'getAllRestaurants', {});

                const soapRes = await fetch('/soap-proxy?service=restaurants&method=getAllRestaurants&body=' +
                    encodeURIComponent(envelope));
                const soapText = await soapRes.text();

                // REST
                const restRes = await fetch('/api/restaurants');
                const restJson = await restRes.json();

                document.getElementById('soapCompare').textContent = formatXml(soapText);
                document.getElementById('restCompare').textContent = JSON.stringify(restJson, null, 2);
                document.getElementById('comparisonArea').classList.remove('hidden');

                setStatus('✅ Comparison loaded', 'success');

            } catch (error) {
                setStatus('❌ Comparison failed: ' + error.message, 'error');
            }
        }
    </script>
</body>
</html>
""";
    }
}