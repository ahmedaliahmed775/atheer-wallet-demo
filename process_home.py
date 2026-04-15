import os
import re

path = r'e:\atheer_project\demo\atheer-wallet-demo\app\src\main\kotlin\com\fintech\app\ui\screens\HomeScreen.kt'
with open(path, 'r', encoding='utf-8') as f:
    text = f.read()

# Remove onVoucher parameter
text = re.sub(r'\s*onVoucher:\s*\(\)\s*->\s*Unit,', '', text)
text = re.sub(r'\s*onVoucher\s*=\s*\{\},', '', text)

# Remove ServiceItem for Voucher (merchant)
text = re.sub(r'\s*ServiceItem\(Icons.Default.CardGiftcard,\s*"قسيمة",\s*Primary,\s*Modifier\.weight\(1f\),\s*onVoucher\)', '', text)

# Remove else branch for customer Voucher in HomeScreen
to_replace = '''
                } else {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ServiceItem(Icons.Default.CardGiftcard, "قسيمة دفع", Color(0xFF534AB7), Modifier.weight(1f), onVoucher)
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.weight(1f))
                    }
                }
'''
if to_replace in text:
    text = text.replace(to_replace, '\n                }')
elif "قسيمة دفع" in text:
    # Use regex
    text = re.sub(r'\s*\} else \{\s*Spacer\(Modifier\.height\(10\.dp\)\)\s*Row\(\s*Modifier\.fillMaxWidth\(\),\s*horizontalArrangement = Arrangement\.spacedBy\(10\.dp\)\s*\)\s*\{\s*ServiceItem\(Icons\.Default\.CardGiftcard,\s*"قسيمة دفع",.*?\)\s*Spacer\(Modifier\.weight\(1f\)\)\s*Spacer\(Modifier\.weight\(1f\)\)\s*\}\s*\}', '\n                }', text, flags=re.DOTALL)

with open(path, 'w', encoding='utf-8') as f:
    f.write(text)

print("HomeScreen updated!")
