var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

function initializeCoreMod() {
    return {
        'durability101': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.ItemRenderer'
            },
            'transformer': function (classNode) {
                var methods = classNode.methods;
                for (m in methods) {
                    var method = methods[m];
                    if (method.name === "renderItemOverlayIntoGUI" || method.name === "func_180453_a") {
                        var instructions = method.instructions;
                        var firstInstruction = instructions.get(0);

                        // Parameters
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 1)); // FontRenderer
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ALOAD, 2)); // ItemStack
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 3)); // xPosition
                        instructions.insertBefore(firstInstruction, new VarInsnNode(Opcodes.ILOAD, 4)); // yPosition

                        // Method
                        var renderDurabilityMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "com/shaybox/durability101/CustomItemRenderer", "renderDurability", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;II)V", false);
                        instructions.insertBefore(firstInstruction, renderDurabilityMethod); // renderDurability
                        break;
                    }
                }
                return classNode;
            }
        }
    }
}