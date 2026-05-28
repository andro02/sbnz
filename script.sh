#!/bin/bash
 
# Sigurnosni pregled Linux sistema — Deo 1, 2 i 3
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'
 
echo "================================================="
echo -e "${GREEN}Započinjem sigurnosni pregled (Deo 1)...${NC}"
echo "================================================="
 
# Upozorenje ukoliko skripta nije pokrenuta kao root
if [ "$EUID" -ne 0 ]; then
  echo -e "${YELLOW}[UPOZORENJE] Skripta nije pokrenuta kao root. Čitanje fajla /etc/shadow i sudoers neće biti moguće.${NC}\n"
fi
 
# 1) Provera verzije kernela
echo -e "${YELLOW}[+] 1. Trenutna verzija kernela i vreme rada sistema:${NC}"
uname -r
KERNEL_MAJOR=$(uname -r | cut -d. -f1)
if [ "$KERNEL_MAJOR" -lt 4 ]; then
    echo -e "${RED}  -> Upozorenje: Kernel je potencijalno zastareo (major verzija < 4).${NC}"
fi
 
UPTIME_DAYS=$(uptime | awk '{for(i=1;i<=NF;i++) if($i=="up") print $(i+1)}' | grep -Eo '[0-9]+' 2>/dev/null)
if [ -n "$UPTIME_DAYS" ] && [ "$UPTIME_DAYS" -gt 30 ]; then
    echo -e "${RED}  -> Upozorenje: Sistem radi $UPTIME_DAYS dana bez restarta. Kernel verovatno nije ažuriran.${NC}"
else
    echo -e "${GREEN}  -> Uptime je razuman, sistem se redovno restartuje.${NC}"
fi
 
# 2) Provera korisnika sa root privilegijama (UID 0)
echo -e "\n${YELLOW}[+] 2. Korisnici sa root privilegijama (UID 0):${NC}"
awk -F: '($3 == "0") {print $1}' /etc/passwd
 
# 3) Provera praznih lozinki
echo -e "\n${YELLOW}[+] 3. Korisnici sa praznom lozinkom (bez autentifikacije):${NC}"
if [ -r /etc/shadow ]; then
    awk -F: '($2 == "" || $2 == "!" || $2 == "!!") {print $1}' /etc/shadow
else
    echo -e "${RED}Nemam dozvolu za čitanje /etc/shadow fajla.${NC}"
fi
 
# 4) Provera korisnika u sudo grupi
echo -e "\n${YELLOW}[+] 4. Korisnici koji imaju pravo na eskalaciju privilegija (sudo grupa):${NC}"
grep '^sudo:.*$' /etc/group | cut -d: -f4
 
# 5) Provera Sudoers pravila (NOPASSWD i ALL)
echo -e "\n${YELLOW}[+] 5. Opasna sudo pravila u konfiguraciji:${NC}"
if [ -r /etc/sudoers ]; then
    NOPASSWD=$(grep -E 'NOPASSWD' /etc/sudoers /etc/sudoers.d/* 2>/dev/null | grep -v '^#')
    ALL_ALL=$(grep -v "^root\|^%sudo\|^#" /etc/sudoers /etc/sudoers.d/* 2>/dev/null | grep -E 'ALL=\(ALL\)')
 
    if [ -n "$NOPASSWD" ]; then
        echo -e "${RED}  -> Pronađena NOPASSWD pravila:${NC}\n$NOPASSWD"
    else
        echo "  -> Nisu pronađena NOPASSWD pravila."
    fi
 
    if [ -n "$ALL_ALL" ]; then
        echo -e "${RED}  -> Upozorenje: Neprivilegovani korisnici imaju ALL=(ALL) pristup:${NC}\n$ALL_ALL"
    fi
else
    echo -e "${RED}Nemam dozvolu za čitanje sudoers konfiguracije.${NC}"
fi
 
# 6) Provera sistemskih naloga sa otvorenim shellom
echo -e "\n${YELLOW}[+] 6. Korisnici sa interaktivnim shell pristupom:${NC}"
grep -E '/bin/(bash|sh|zsh)$' /etc/passwd | cut -d: -f1
 
# 7) Provera korisnika u 'adm' grupi
echo -e "\n${YELLOW}[+] 7. Korisnici sa pravom čitanja sistemskih logova (adm grupa):${NC}"
grep '^adm:.*$' /etc/group | cut -d: -f4
 
# 8) Provera algoritma za enkripciju lozinki
echo -e "\n${YELLOW}[+] 8. Algoritmi šifrovanja lozinki u /etc/shadow:${NC}"
if [ -r /etc/shadow ]; then
    while IFS=: read -r user hash rest; do
        [ -z "$hash" ] || [ "$hash" = "*" ] || [ "$hash" = "!" ] || [ "$hash" = "!!" ] && continue
        if echo "$hash" | grep -q '^\$6\$'; then
            echo -e "${GREEN}  $user koristi siguran SHA-512 algoritam.${NC}"
        elif echo "$hash" | grep -q '^\$1\$'; then
            echo -e "${RED}  $user koristi ranjivi MD5 algoritam - lako se razbija!${NC}"
        elif echo "$hash" | grep -qv '^\$'; then
            echo -e "${RED}  $user koristi DES algoritam - veoma nesigurno!${NC}"
        fi
    done < /etc/shadow
else
    echo -e "${RED}Nemam dozvolu za čitanje /etc/shadow fajla.${NC}"
fi
 
# 9) Provera politike i kompleksnosti lozinki (PAM konfiguracija)
echo -e "\n${YELLOW}[+] 9. Politika kompleksnosti lozinki (PAM konfiguracija):${NC}"
PAM_FILE="/etc/pam.d/common-password"
 
if [ -f "$PAM_FILE" ]; then
    if grep -qE '^password\s+.*sha512' "$PAM_FILE" 2>/dev/null; then
        echo -e "${GREEN}  -> Podrazumevani algoritam za nove lozinke je bezbedan (SHA-512).${NC}"
    elif grep -qE '^password\s+.*md5' "$PAM_FILE" 2>/dev/null; then
        echo -e "${RED}  -> Upozorenje: Podrazumevani algoritam za nove lozinke je ranjivi MD5!${NC}"
    else
        echo "  -> Podrazumevani algoritam za nove lozinke nije eksplicitno podešen na SHA-512 u ovom fajlu."
    fi
 
    COMPLEXITY=$(grep -E 'pam_cracklib.so|pam_pwquality.so' "$PAM_FILE" 2>/dev/null | grep -v '^#')
    if [ -n "$COMPLEXITY" ]; then
        echo -e "${GREEN}  -> Modul za kompleksnost lozinki je aktivan:${NC}\n    $COMPLEXITY"
    else
        echo -e "${RED}  -> Upozorenje: Modul za kompleksnost lozinki (pam_cracklib) nije pronađen! Korisnici mogu postavljati veoma slabe lozinke.${NC}"
    fi
else
    echo -e "${RED}  -> Fajl $PAM_FILE nije pronađen.${NC}"
fi
 
echo -e "\n================================================="
echo -e "${GREEN}Deo 1 završen.${NC}"
 
# =================================================================
echo ""
echo "================================================="
echo -e "${GREEN}Započinjem sigurnosni pregled (Deo 2 — Mrežni pregled i Firewall)...${NC}"
echo "================================================="
 
# 10) Mrežni interfejsi
# Bezbednosni cilj: identifikacija svih aktivnih interfejsa i IP adresa.
# Neočekivani interfejsi mogu ukazivati na kompromitovan sistem.
echo -e "\n${YELLOW}[+] 10. Mrežni interfejsi:${NC}"
if command -v ip &>/dev/null; then
    ip addr show 2>/dev/null
elif command -v ifconfig &>/dev/null; then
    ifconfig -a 2>/dev/null
else
    echo -e "${RED}  -> Nije pronađena komanda ip ni ifconfig.${NC}"
fi
 
# 11) Tabela rutiranja
# Bezbednosni cilj: neočekivane rute mogu ukazivati na pogrešnu konfiguraciju
# ili na napadača koji je promenio rutiranje radi presretanja saobraćaja.
echo -e "\n${YELLOW}[+] 11. Tabela rutiranja:${NC}"
if command -v route &>/dev/null; then
    route -n 2>/dev/null
else
    netstat -rn 2>/dev/null
fi
 
# 12) DNS konfiguracija
# Bezbednosni cilj: kompromitovani /etc/resolv.conf ili /etc/hosts mogu preusmeriti
# saobraćaj na maliciozne servere (DNS hijacking / hosts poisoning).
echo -e "\n${YELLOW}[+] 12. DNS konfiguracija (/etc/resolv.conf i /etc/hosts):${NC}"
echo "  --- /etc/resolv.conf ---"
cat /etc/resolv.conf 2>/dev/null
echo "  --- /etc/hosts ---"
cat /etc/hosts 2>/dev/null
 
SUSPICIOUS=$(grep -vE '^\s*#|^\s*$|^127\.|^::1|^0\.0\.0\.0' /etc/hosts 2>/dev/null | grep -vE '^192\.168\.|^10\.|^172\.')
if [ -n "$SUSPICIOUS" ]; then
    echo -e "${RED}  -> Upozorenje: Potencijalno sumnjivi unosi u /etc/hosts:${NC}"
    echo "$SUSPICIOUS"
else
    echo -e "${GREEN}  -> /etc/hosts ne sadrži sumnjive nestandardne unose.${NC}"
fi
 
# 13) IPv4 firewall pravila (iptables)
# Bezbednosni cilj: provera da li su INPUT/OUTPUT politike restriktivne i
# da li su osetljivi portovi (npr. SSH) dostupni sa svih IP adresa.
echo -e "\n${YELLOW}[+] 13. IPv4 firewall pravila (iptables):${NC}"
if command -v iptables &>/dev/null; then
    iptables -L -v -n 2>/dev/null
 
    INPUT_POLICY=$(iptables -L INPUT 2>/dev/null | head -1 | grep -oP 'policy \K\w+')
    if [ "$INPUT_POLICY" = "DROP" ] || [ "$INPUT_POLICY" = "REJECT" ]; then
        echo -e "${GREEN}  -> INPUT default politika je restriktivna: $INPUT_POLICY${NC}"
    else
        echo -e "${RED}  -> Upozorenje: INPUT default politika je '$INPUT_POLICY' — preporučuje se DROP.${NC}"
    fi
 
    OUTPUT_POLICY=$(iptables -L OUTPUT 2>/dev/null | head -1 | grep -oP 'policy \K\w+')
    if [ "$OUTPUT_POLICY" = "DROP" ] || [ "$OUTPUT_POLICY" = "REJECT" ]; then
        echo -e "${GREEN}  -> OUTPUT default politika je restriktivna: $OUTPUT_POLICY${NC}"
    else
        echo -e "${YELLOW}  -> Napomena: OUTPUT default politika je '$OUTPUT_POLICY' — razmotriti restrikciju izlaznog saobraćaja.${NC}"
    fi
 
    if iptables -L INPUT -n 2>/dev/null | grep -qE "dpt:22\s+ACCEPT"; then
        if iptables -L INPUT -n 2>/dev/null | grep -E "dpt:22\s+ACCEPT" | grep -q "0\.0\.0\.0/0"; then
            echo -e "${RED}  -> Upozorenje: SSH (port 22) je dostupan sa SVIH IP adresa — preporučiti whitelist pouzdanih adresa.${NC}"
        else
            echo -e "${GREEN}  -> SSH pristup je ograničen na određene IP adrese.${NC}"
        fi
    fi
else
    echo -e "${RED}  -> iptables nije dostupan ili nema privilegija.${NC}"
fi
 
# 14) Perzistentnost firewall pravila nakon rebuta
# Bezbednosni cilj: ako pravila nisu sačuvana, server ostaje nezaštićen
# pri sledećem pokretanju sve dok se pravila ručno ne primene.
echo -e "\n${YELLOW}[+] 14. Perzistentnost firewall pravila nakon rebuta:${NC}"
PERSISTENT=false
 
if [ -f /etc/network/if-pre-up.d/iptables ]; then
    echo -e "${GREEN}  -> Pronađen /etc/network/if-pre-up.d/iptables — pravila se učitavaju pri startu.${NC}"
    PERSISTENT=true
    RULES_FILE=$(grep -oP "< \K[^\s]+" /etc/network/if-pre-up.d/iptables 2>/dev/null | head -1)
    if [ -n "$RULES_FILE" ] && [ -f "$RULES_FILE" ]; then
        ACTIVE=$(iptables-save 2>/dev/null | grep -vE "^#|^:" | sort)
        SAVED=$(grep -vE "^#|^:" "$RULES_FILE" 2>/dev/null | sort)
        if [ "$ACTIVE" = "$SAVED" ]; then
            echo -e "${GREEN}  -> Aktivna i sačuvana pravila su identična.${NC}"
        else
            echo -e "${RED}  -> Upozorenje: Aktivna i sačuvana pravila se RAZLIKUJU — promene neće preživeti reboot.${NC}"
        fi
    fi
fi
 
if systemctl is-active --quiet netfilter-persistent 2>/dev/null || \
   systemctl is-active --quiet iptables 2>/dev/null; then
    echo -e "${GREEN}  -> Servis za perzistentnost pravila je aktivan (netfilter-persistent/iptables).${NC}"
    PERSISTENT=true
fi
 
if [ "$PERSISTENT" = false ]; then
    echo -e "${RED}  -> Upozorenje: Firewall pravila NISU perzistentna — neće preživeti reboot!${NC}"
fi
 
# 15) IPv6 firewall i status
# Bezbednosni cilj: napadač može zaobići IPv4 firewall ako IPv6 ostane
# nezaštićen ili aktivan bez odgovarajućih pravila.
echo -e "\n${YELLOW}[+] 15. IPv6 — firewall i status:${NC}"
if command -v ip6tables &>/dev/null; then
    ip6tables -L -v -n 2>/dev/null
 
    IP6_RULES=$(ip6tables -L 2>/dev/null | grep -vcE "^Chain|^target|^\s*$")
    if [ "$IP6_RULES" -eq 0 ] 2>/dev/null; then
        IPV6_DISABLED=$(cat /proc/sys/net/ipv6/conf/all/disable_ipv6 2>/dev/null)
        if [ "$IPV6_DISABLED" = "1" ]; then
            echo -e "${GREEN}  -> IPv6 je onemogućen na nivou kernela.${NC}"
        else
            echo -e "${RED}  -> Upozorenje: IPv6 je AKTIVAN ali nema ip6tables pravila — sistem je nezaštićen na IPv6!${NC}"
            echo -e "     Preporuka: primeniti ista pravila kao za IPv4, ili onemogućiti IPv6:"
            echo -e "     echo 'net.ipv6.conf.all.disable_ipv6=1' > /etc/sysctl.d/disableipv6.conf"
        fi
    else
        IP6_INPUT=$(ip6tables -L INPUT 2>/dev/null | head -1 | grep -oP 'policy \K\w+')
        if [ "$IP6_INPUT" = "DROP" ] || [ "$IP6_INPUT" = "REJECT" ]; then
            echo -e "${GREEN}  -> IPv6 INPUT politika je restriktivna: $IP6_INPUT${NC}"
        else
            echo -e "${YELLOW}  -> Napomena: IPv6 INPUT politika je '$IP6_INPUT' — razmotriti restrikciju.${NC}"
        fi
    fi
else
    echo -e "${YELLOW}  -> ip6tables nije dostupan.${NC}"
fi
 
# 16) Servisi koji slušaju na mreži
# Bezbednosni cilj: svaki servis koji sluša na 0.0.0.0 dostupan je sa
# svih mrežnih interfejsa — nepotrebni servisi povećavaju napadnu površinu.
echo -e "\n${YELLOW}[+] 16. Servisi koji slušaju na mreži (otvoreni portovi):${NC}"
if command -v ss &>/dev/null; then
    ss -tlnup 2>/dev/null
elif command -v netstat &>/dev/null; then
    netstat -tlnup 2>/dev/null
fi
 
OPEN_ALL=$(ss -tlnup 2>/dev/null | grep LISTEN | grep -E "0\.0\.0\.0|::")
if [ -n "$OPEN_ALL" ]; then
    echo -e "${YELLOW}  -> Napomena: Sledeći servisi slušaju na SVIM interfejsima (0.0.0.0 ili ::):${NC}"
    echo "$OPEN_ALL"
fi
 
echo -e "\n================================================="
echo -e "${GREEN}Deo 2 završen.${NC}"