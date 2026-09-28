import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../../environments/environment.development';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private socket!: WebSocket;
  stoarge = inject(StorageService);
  entreprise_id:any
  private apiUrl = environment.urlSocket;
// Signal qui contient la liste des messages
  private chatsSignal = signal<{
    id?: number,
    sender_id: number,
    receiver_id: number,
    content: string,
    timestamp: string,
    entreprise_id: string
  }[]>([]);

  public chats = this.chatsSignal.asReadonly();

  constructor() {
        this.entreprise_id = this.stoarge.getEntrepriseID();
    // this.connect();
  }
/** Reset les messages */
  resetChats() {
    this.chatsSignal.set([]);
  }
  public connect(user_id :any) {
    // Avant de se reconnecter → reset l’ancien contenu
    this.resetChats();
    this.socket = new WebSocket(`${this.apiUrl}/ws/chat/${this.entreprise_id}/${user_id}`);

    this.socket.onopen = () => {
      
      console.log('✅ WebSocket connecté');
    };

    this.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
 this.chatsSignal.update((chats) => [
      ...chats,
      {
        sender_id: data.sender_id,
        receiver_id: data.receiver_id,
        content: data.content,
        timestamp: data.timestamp,
        entreprise_id: data.entreprise_id,
      }
    ]);
        // Vérifier si c'est bien un "new_message"
        // if (data.action === 'new_message' && data.message) {
        //   const msg = data.message;

        //   // Mettre à jour la liste des chats
        //   this.chatsSignal.update((chats) => [...chats, msg]);
        // }
      } catch (error) {
        console.error('❌ Erreur de parsing WebSocket:', error);
      }
    };

    this.socket.onerror = (error) => {
      console.error('❌ Erreur WebSocket', error);
    };

    this.socket.onclose = (event) => {
      console.warn('⚠️ WebSocket déconnecté', event.reason);
      // tentative de reconnexion auto
      setTimeout(() => this.connect(user_id), 3000);
    };
  }

  closeWebSocket() {
    if (this.socket) {
      this.socket.close();
      console.log('🔌 WebSocket fermé proprement');
    }
  }

  sendMessage(receiver_id: number, content: string, entreprise_id: string) {
    if (this.socket.readyState === WebSocket.OPEN) {
      const payload = {
        action: 'send_message',
        sender_id:this.entreprise_id,
        receiver_id,
        content,
        entreprise_id
      };
      this.socket.send(JSON.stringify(payload));
    } else {
      console.warn('⚠️ Impossible d’envoyer le message, WebSocket non connecté');
    }
  }
}