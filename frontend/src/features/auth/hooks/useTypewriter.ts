import { useEffect, useState } from "react";

export function useTypewriter(messages: string[]) {
  const [text, setText] = useState("");
  const [index, setIndex] = useState(0);
  const [subIndex, setSubIndex] = useState(0);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const currentMessage = messages[index];
    if (!currentMessage) return;

    if (!deleting && subIndex === currentMessage.length) {
      const timeout = setTimeout(() => setDeleting(true), 1200);
      return () => clearTimeout(timeout);
    }

    if (deleting && subIndex === 0) {
      setDeleting(false);
      setIndex((prev) => (prev + 1) % messages.length);
      return;
    }

    const timeout = setTimeout(
      () => {
        setSubIndex((prev) => prev + (deleting ? -1 : 1));
        setText(currentMessage.substring(0, subIndex));
      },
      deleting ? 30 : 50,
    );

    return () => clearTimeout(timeout);
  }, [subIndex, index, deleting, messages]);

  return text;
}