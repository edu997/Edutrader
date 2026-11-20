//+------------------------------------------------------------------+
//| Ed Trading Pro - MT5 Read-Only Bridge                            |
//| Sends terminal/account heartbeat to the secure gateway.          |
//| This EA DOES NOT place, modify, or close trades.                 |
//+------------------------------------------------------------------+
#property strict
#property version   "0.1.0"
#property description "Ed Trading Pro read-only MT5 heartbeat bridge"

input string InpGatewayBaseUrl = "";       // HTTPS base URL, e.g. https://api.example.com
input string InpTerminalToken  = "";       // Per-terminal token issued by Ed Trading Pro
input string InpTerminalAlias  = "My MT5 VPS";
input int    InpHeartbeatSeconds = 30;
input int    InpHttpTimeoutMs = 5000;

bool g_warned_config = false;

string JsonEscape(const string value)
{
   string out = "";
   for(int i = 0; i < StringLen(value); i++)
   {
      ushort ch = StringGetCharacter(value, i);
      if(ch == 34)       out += "\\\"";
      else if(ch == 92)  out += "\\\\";
      else if(ch == 10)  out += "\\n";
      else if(ch == 13)  out += "\\r";
      else if(ch == 9)   out += "\\t";
      else               out += ShortToString(ch);
   }
   return out;
}

string BuildHeartbeatPayload()
{
   long login = AccountInfoInteger(ACCOUNT_LOGIN);
   long build = TerminalInfoInteger(TERMINAL_BUILD);
   bool connected = (bool)TerminalInfoInteger(TERMINAL_CONNECTED);

   string payload = "{";
   payload += "\"schema_version\":1,";
   payload += "\"terminal_alias\":\"" + JsonEscape(InpTerminalAlias) + "\",";
   payload += "\"account_login\":" + LongToString(login) + ",";
   payload += "\"account_server\":\"" + JsonEscape(AccountInfoString(ACCOUNT_SERVER)) + "\",";
   payload += "\"broker_company\":\"" + JsonEscape(AccountInfoString(ACCOUNT_COMPANY)) + "\",";
   payload += "\"account_currency\":\"" + JsonEscape(AccountInfoString(ACCOUNT_CURRENCY)) + "\",";
   payload += "\"balance\":" + DoubleToString(AccountInfoDouble(ACCOUNT_BALANCE), 2) + ",";
   payload += "\"equity\":" + DoubleToString(AccountInfoDouble(ACCOUNT_EQUITY), 2) + ",";
   payload += "\"margin\":" + DoubleToString(AccountInfoDouble(ACCOUNT_MARGIN), 2) + ",";
   payload += "\"free_margin\":" + DoubleToString(AccountInfoDouble(ACCOUNT_MARGIN_FREE), 2) + ",";
   payload += "\"leverage\":" + LongToString(AccountInfoInteger(ACCOUNT_LEVERAGE)) + ",";
   payload += "\"terminal_build\":" + LongToString(build) + ",";
   payload += "\"terminal_connected\":" + (connected ? "true" : "false") + ",";
   payload += "\"terminal_company\":\"" + JsonEscape(TerminalInfoString(TERMINAL_COMPANY)) + "\"";
   payload += "}";
   return payload;
}

void SendHeartbeat()
{
   if(StringLen(InpGatewayBaseUrl) == 0 || StringLen(InpTerminalToken) == 0)
   {
      if(!g_warned_config)
      {
         Print("Ed Trading Pro: configure HTTPS gateway URL and terminal token. No data has been sent.");
         g_warned_config = true;
      }
      return;
   }

   if(StringFind(InpGatewayBaseUrl, "https://") != 0)
   {
      Print("Ed Trading Pro: HTTPS is required. Heartbeat not sent.");
      return;
   }

   string url = InpGatewayBaseUrl;
   if(StringSubstr(url, StringLen(url) - 1, 1) == "/")
      url = StringSubstr(url, 0, StringLen(url) - 1);
   url += "/v1/mt5/heartbeat";

   string headers = "Content-Type: application/json\r\n";
   headers += "Authorization: Bearer " + InpTerminalToken + "\r\n";

   char request[];
   char response[];
   string response_headers;
   string payload = BuildHeartbeatPayload();
   int copied = StringToCharArray(payload, request, 0, WHOLE_ARRAY, CP_UTF8);
   if(copied > 0)
      ArrayResize(request, copied - 1); // Remove terminating NUL.

   ResetLastError();
   int status = WebRequest("POST", url, headers, InpHttpTimeoutMs,
                           request, response, response_headers);

   if(status == -1)
   {
      Print("Ed Trading Pro: heartbeat request failed. MQL5 error=", GetLastError(),
            ". Check WebRequest allow-list, VPS internet and gateway URL.");
      return;
   }

   if(status < 200 || status >= 300)
      Print("Ed Trading Pro: gateway returned HTTP ", status, ". Response=", CharArrayToString(response));
}

int OnInit()
{
   if(InpHeartbeatSeconds < 10)
   {
      Print("Ed Trading Pro: heartbeat interval must be at least 10 seconds.");
      return INIT_PARAMETERS_INCORRECT;
   }

   EventSetTimer(InpHeartbeatSeconds);
   Print("Ed Trading Pro read-only bridge initialized. Trading functions are not implemented.");
   SendHeartbeat();
   return INIT_SUCCEEDED;
}

void OnTimer()
{
   SendHeartbeat();
}

void OnDeinit(const int reason)
{
   EventKillTimer();
   Print("Ed Trading Pro bridge stopped. Reason=", reason);
}
